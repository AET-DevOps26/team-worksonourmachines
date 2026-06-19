import { execFileSync } from 'node:child_process';
import { existsSync, mkdtempSync, readdirSync, rmSync, statSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join, resolve } from 'node:path';

const testRoot = 'test-dependency-cruiser';
const config = '.dependency-cruiser.cjs';
const rootTsConfig = resolve('tsconfig.json');

function createCaseConfig(casePath) {
    const tempDir = mkdtempSync(join(tmpdir(), 'depcruise-test-'));
    const tsConfigPath = join(tempDir, 'tsconfig.json');
    const configPath = join(tempDir, 'dependency-cruiser.cjs');

    writeFileSync(
        tsConfigPath,
        JSON.stringify(
            {
                compilerOptions: {
                    baseUrl: resolve(casePath),
                    paths: {
                        '~/*': ['app/*'],
                    },
                },
                extends: rootTsConfig,
                include: [resolve(casePath, 'app/**/*'), resolve(casePath, 'app/**/.server/**/*')],
            },
            null,
            2,
        ),
    );

    writeFileSync(
        configPath,
        `const baseConfig = require(${JSON.stringify(resolve(config))});\n\nmodule.exports = {\n    ...baseConfig,\n    options: {\n        ...baseConfig.options,\n        tsConfig: {\n            fileName: ${JSON.stringify(tsConfigPath)},\n        },\n    },\n};\n`,
    );

    return { configPath, tempDir };
}

function findCases(dir, ruleName = null) {
    const cases = [];

    for (const entry of readdirSync(dir)) {
        const fullPath = join(dir, entry);

        if (!statSync(fullPath).isDirectory()) {
            continue;
        }

        const appPath = join(fullPath, 'app');

        if (existsSync(appPath)) {
            cases.push({ appPath, casePath: fullPath, ruleName: ruleName ?? entry });
            continue;
        }

        cases.push(...findCases(fullPath, ruleName ?? entry));
    }

    return cases;
}

const cases = findCases(testRoot);

if (cases.length === 0) {
    console.error(`No dependency-cruiser test cases found under ${testRoot}/`);
    process.exit(1);
}

let failed = 0;

for (const { ruleName, casePath, appPath } of cases) {
    const label = casePath.replace(`${testRoot}/`, '');
    const { configPath, tempDir } = createCaseConfig(casePath);

    try {
        const output = execFileSync(
            'pnpm',
            ['exec', 'depcruise', appPath, '--config', configPath, '--output-type', 'json'],
            {
                encoding: 'utf8',
            },
        );

        const report = JSON.parse(output);
        const violations = report.summary?.violations ?? [];
        const matching = violations.filter((violation) => violation.rule.name === ruleName);

        if (matching.length === 0) {
            console.error(`FAIL ${label}: expected rule "${ruleName}" to trigger`);
            for (const violation of violations) {
                console.error(`  - ${violation.rule.name}: ${violation.from} -> ${violation.to}`);
            }
            failed += 1;
            continue;
        }

        console.log(`ok ${label}`);
    } finally {
        rmSync(tempDir, { force: true, recursive: true });
    }
}

if (failed > 0) {
    console.error(`\n${failed} dependency-cruiser rule tests failed`);
    process.exit(1);
}

console.log(`\nAll ${cases.length} dependency-cruiser rule tests passed`);
