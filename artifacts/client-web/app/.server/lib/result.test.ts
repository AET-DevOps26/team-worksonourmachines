import { describe, expect, it } from 'vitest';
import { err, isErr, isOk, ok, wrapAsyncResult, wrapAsyncThrowing, wrapThrowing } from './result';

describe('result', () => {
    describe('ok', () => {
        it('exposes value and Ok flags', () => {
            const result = ok(42);

            expect(result.isOk).toBe(true);
            expect(result.isErr).toBe(false);
            expect(result.value).toBe(42);
            expect(isOk(result)).toBe(true);
            expect(isErr(result)).toBe(false);
        });

        it('maps the value', () => {
            expect(
                ok(2)
                    .map((n) => n * 3)
                    .unwrap(),
            ).toBe(6);
        });

        it('leaves mapErr unchanged', () => {
            const result = ok('keep').mapErr(() => 'ignored');

            expect(result.isOk).toBe(true);
            if (result.isOk) {
                expect(result.value).toBe('keep');
            }
        });

        it('chains with andThen', () => {
            const result = ok(2).andThen((n) => ok(n + 1));

            expect(result.isOk).toBe(true);
            if (result.isOk) {
                expect(result.value).toBe(3);
            }
        });

        it('propagates Err from andThen', () => {
            const result = ok(2).andThen(() => err('boom'));

            expect(result.isErr).toBe(true);
            if (result.isErr) {
                expect(result.error).toBe('boom');
            }
        });

        it('unwraps the value', () => {
            expect(ok('yes').unwrap()).toBe('yes');
            expect(ok('yes').unwrapOr('default')).toBe('yes');
            expect(ok('yes').unwrapOrElse(() => 'default')).toBe('yes');
        });
    });

    describe('err', () => {
        it('exposes error and Err flags', () => {
            const result = err('fail');

            expect(result.isOk).toBe(false);
            expect(result.isErr).toBe(true);
            expect(result.error).toBe('fail');
            expect(isOk(result)).toBe(false);
            expect(isErr(result)).toBe(true);
        });

        it('skips map unless an error mapper is provided', () => {
            const unchanged = err('fail').map((n: number) => n * 2);
            expect(unchanged.isErr).toBe(true);
            if (unchanged.isErr) {
                expect(unchanged.error).toBe('fail');
            }

            const mapped = err('fail').map(
                (_value: never) => _value,
                (e) => `mapped:${e}`,
            );
            expect(mapped.isErr).toBe(true);
            if (mapped.isErr) {
                expect(mapped.error).toBe('mapped:fail');
            }
        });

        it('maps the error with mapErr', () => {
            const result = err('fail').mapErr((e) => e.toUpperCase());

            expect(result.isErr).toBe(true);
            if (result.isErr) {
                expect(result.error).toBe('FAIL');
            }
        });

        it('short-circuits andThen', () => {
            const result = err('fail').andThen();

            expect(result.isErr).toBe(true);
            if (result.isErr) {
                expect(result.error).toBe('fail');
            }
        });

        it('unwrapOr and unwrapOrElse return defaults', () => {
            expect(err('fail').unwrapOr('fallback')).toBe('fallback');
            expect(err('fail').unwrapOrElse(() => 'computed')).toBe('computed');
        });

        it('unwrap throws the error', () => {
            expect(() => err(new Error('boom')).unwrap()).toThrow('boom');
        });
    });

    describe('wrapThrowing', () => {
        it('returns Ok for successful calls', () => {
            const parse = wrapThrowing((input: string) => JSON.parse(input) as { a: number });
            const result = parse('{"a":1}');

            expect(result.isOk).toBe(true);
            if (result.isOk) {
                expect(result.value).toEqual({ a: 1 });
            }
        });

        it('returns Err for thrown errors', () => {
            const parse = wrapThrowing((input: string) => JSON.parse(input));
            const result = parse('not-json');

            expect(result.isErr).toBe(true);
            if (result.isErr) {
                expect(result.error).toBeInstanceOf(Error);
            }
        });
    });

    describe('wrapAsyncThrowing', () => {
        it('returns Ok for resolved promises', async () => {
            const load = wrapAsyncThrowing(async (value: number) => value * 2);
            const result = await load(21);

            expect(result.isOk).toBe(true);
            if (result.isOk) {
                expect(result.value).toBe(42);
            }
        });

        it('returns Err for rejected promises', async () => {
            const load = wrapAsyncThrowing(async () => {
                throw new Error('network');
            });
            const result = await load();

            expect(result.isErr).toBe(true);
            if (result.isErr) {
                expect(result.error).toMatchObject({ message: 'network' });
            }
        });
    });

    describe('wrapAsyncResult', () => {
        it('chains map and andThen without intermediate awaits', async () => {
            const result = await wrapAsyncResult(Promise.resolve(ok(2)))
                .map((n) => n + 1)
                .andThen((n) => ok(n * 10));

            expect(result.isOk).toBe(true);
            if (result.isOk) {
                expect(result.value).toBe(30);
            }
        });

        it('maps errors asynchronously', async () => {
            const result = await wrapAsyncResult(Promise.resolve(err('nope'))).mapErr((e) => `mapped:${e}`);

            expect(result.isErr).toBe(true);
            if (result.isErr) {
                expect(result.error).toBe('mapped:nope');
            }
        });

        it('supports unwrapOr on the wrapper', async () => {
            await expect(wrapAsyncResult(Promise.resolve(err('x'))).unwrapOr('default')).resolves.toBe('default');
            await expect(wrapAsyncResult(Promise.resolve(ok('y'))).unwrapOr('default')).resolves.toBe('y');
        });
    });
});
