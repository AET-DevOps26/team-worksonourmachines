import winston from 'winston';
import { env } from '~/.server/lib/env';

const prettyFormat = winston.format.combine(
    winston.format.timestamp(),
    winston.format.metadata(),
    winston.format.prettyPrint({ colorize: true }),
);

const jsonFormat = winston.format.combine(
    winston.format.metadata(),
    winston.format((info) => {
        info.level = winston.config.npm.levels[info.level]!.toString();
        return info;
    })(),
    winston.format.json(),
);

export type LogLevel = 'debug' | 'info' | 'warn' | 'error';
export type LogFormat = 'pretty' | 'json';

export function createLogger(logFormat: LogFormat, logLevel: LogLevel) {
    const format = logFormat === 'pretty' ? prettyFormat : jsonFormat;
    const level = logLevel;
    return winston.createLogger({
        format,
        level,
        levels: winston.config.npm.levels,
        transports: [new winston.transports.Console()],
    });
}

export type Logger = ReturnType<typeof createLogger>;
export const logger = createLogger(env.get('LOG_FORMAT'), env.get('LOG_LEVEL'));
