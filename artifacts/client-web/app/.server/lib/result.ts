interface IResult<T, E = Error> {
    readonly isOk: boolean;
    readonly isErr: boolean;

    /**
     * Maps the value if this is Ok, otherwise returns the error unchanged.
     */
    map<U>(fn: (value: T) => U): Result<U, E>;
    map<U, F>(fn: (value: T) => U, fnErr: (error: E) => F): Result<U, F>;
    /**
     * Maps the error if this is Err, otherwise returns the value unchanged.
     */
    mapErr<F>(fn: (error: E) => F): Result<T, F>;
    /**
     * Chains another Result-returning operation if this is Ok.
     */
    andThen<U, F>(fn: (value: T) => Result<U, F>): Result<U, F>;
    /**
     * Returns the value if Ok, otherwise returns the provided default.
     */
    unwrapOr<U>(defaultValue: U): T | U;
    /**
     * Returns the value if Ok, otherwise throws.
     * @throws
     */
    unwrap(): T;
    /**
     * Returns the value if Ok, otherwise returns the provided default via callback.
     */
    unwrapOrElse<U>(fn: () => U): T | U;
}

export type Result<T, E = Error> = Ok<T> | Err<E>;

export type AsyncResult<T, E = Error> = Promise<Result<T, E>>;

/**
 * Success variant of Result
 */
export class Ok<T> implements IResult<T, never> {
    readonly isOk: true = true;
    readonly isErr: false = false;
    readonly value: T;

    constructor(value: T) {
        this.value = value;
    }

    map<U>(fn: (value: T) => U): Result<U, never> {
        return ok(fn(this.value));
    }

    mapErr<F>(_fn: (error: never) => F): Result<T, F> {
        // as unknown as Result<T, F> required for type compatibility
        return this as unknown as Result<T, F>;
    }

    andThen<U, F>(fn: (value: T) => Result<U, F>): Result<U, F> {
        return fn(this.value);
    }

    unwrapOr<U>(_default: U): T {
        return this.value;
    }

    unwrap(): T {
        return this.value;
    }

    unwrapOrElse<U>(_fn: () => U): T {
        return this.value;
    }
}

/**
 * Error variant of Result
 */
export class Err<E = Error> implements IResult<never, E> {
    readonly isOk: false = false;
    readonly isErr: true = true;
    readonly error: E;

    constructor(error: E) {
        this.error = error;
    }

    map<U>(_fn: (value: never) => U): Result<U, E>;
    map<F>(_fn: (value: never) => never, fnErr: (error: E) => F): Result<never, F>;
    map<Er>(_fn: (value: never) => never, fnErr?: (error: E) => Er): Result<never, Er> {
        if (fnErr) {
            return err(fnErr(this.error));
        }
        return this as unknown as Result<never, Er>;
    }

    mapErr<F>(fn: (error: E) => F): Result<never, F> {
        return err(fn(this.error));
    }

    andThen<F>(): Result<never, F> {
        return this as unknown as Result<never, F>;
    }

    unwrapOr<U>(defaultValue: U): U {
        return defaultValue;
    }

    unwrap(): never {
        throw this.error;
    }

    unwrapOrElse<U>(fn: () => U): U {
        return fn();
    }
}

/**
 * Creates an Ok result.
 *
 * @param value - The success value
 * @returns An Ok result containing the value
 */
export function ok<T>(value: T): Ok<T> {
    return new Ok(value);
}

/**
 * Creates an Err result.
 *
 * @param error - The error value
 * @returns An Err result containing the error
 */
export function err<E = Error>(error: E): Err<E> {
    return new Err(error);
}

/**
 * Type guard to check if a Result is Ok.
 */
export function isOk<T, E>(result: Result<T, E>): result is Ok<T> {
    return result.isOk;
}

/**
 * Type guard to check if a Result is Err.
 */
export function isErr<T, E>(result: Result<T, E>): result is Err<E> {
    return result.isErr;
}

/**
 * Wraps a function that may throw, converting it to return a Result.
 *
 * @param fn - The function to wrap
 * @returns A function that returns a Result instead of throwing
 *
 * @example
 * ```ts
 * const safeParse = wrapThrowing((str: string) => JSON.parse(str));
 * const result = safeParse('{"key": "value"}');
 * ```
 */
export function wrapThrowing<T, Args extends unknown[]>(fn: (...args: Args) => T): (...args: Args) => Result<T, Error> {
    return (...args: Args): Result<T, Error> => {
        try {
            return ok(fn(...args));
        } catch (error) {
            return err(error instanceof Error ? error : new Error(String(error)));
        }
    };
}

/**
 * Wraps a Promise-returning function to return a Result.
 *
 * @param fn - The async function to wrap
 * @returns A function that returns a Promise<Result> instead of throwing
 *
 * @example
 * ```ts
 * const safeFetch = wrapAsyncThrowing(fetch);
 * const result = await safeFetch('https://api.example.com');
 * ```
 */
export function wrapAsyncThrowing<T, Args extends unknown[], E = Error>(
    fn: (...args: Args) => Promise<T>,
): (...args: Args) => Promise<Result<T, E | Error>> {
    return async (...args: Args): Promise<Result<T, E | Error>> => {
        try {
            const value = await fn(...args);
            return ok(value);
        } catch (error) {
            return err(error instanceof Error ? error : new Error(String(error)));
        }
    };
}

export type AsyncResultWrapper<T, E = Error> = PromiseLike<Result<T, E>> & {
    map<U>(fn: (value: T) => U): AsyncResultWrapper<U, E>;
    mapErr<F>(fn: (error: E) => F): AsyncResultWrapper<T, F>;
    andThen<U, F>(fn: (value: T) => Result<U, F>): AsyncResultWrapper<U, E | F>;
    andThenAsync<U, F>(fn: (value: T) => AsyncResult<U, F>): AsyncResultWrapper<U, E | F>;
    unwrapOr<D>(defaultValue: D): Promise<T | D>;
    unwrapOrElse<D>(fn: () => D): Promise<T | D>;
    unwrap(): Promise<T>;
};

/**
 * Wraps a Promise&lt;Result&gt; in an object with Result-like methods. Chain operations without awaiting until the end.
 *
 * @example
 * ```ts
 * const result = await wrapAsyncResult(verifyToken())
 *     .mapErr(() => 'No valid user');
 * if (isErr(result)) return err(result.error);
 * ```
 */
export function wrapAsyncResult<T, E = Error>(promise: AsyncResult<T, E>): AsyncResultWrapper<T, E> {
    function wrap<T, E>(p: AsyncResult<T, E>): AsyncResultWrapper<T, E> {
        const wrapper = {
            andThen<U, F>(fn: (value: T) => Result<U, F>) {
                return wrap<U, E | F>(p.then((r) => (r.isOk ? fn(r.value) : (r as unknown as Result<U, E | F>))));
            },
            andThenAsync<U, F>(fn: (value: T) => AsyncResult<U, F>) {
                return wrap<U, E | F>(
                    p.then((r) => (r.isOk ? fn(r.value) : (Promise.resolve(r) as AsyncResult<U, E | F>))),
                );
            },
            map<U>(fn: (value: T) => U) {
                return wrap<U, E>(p.then((r) => r.map(fn)));
            },
            mapErr<F>(fn: (error: E) => F) {
                return wrap<T, F>(p.then((r) => r.mapErr(fn)));
            },
            // Intentional thenable for await support
            // biome-ignore lint/suspicious/noThenProperty: required for await to resolve to Result
            then<TResult1 = Result<T, E>, TResult2 = never>(
                onfulfilled?: ((value: Result<T, E>) => TResult1 | PromiseLike<TResult1>) | null,
                onrejected?: ((reason: unknown) => TResult2 | PromiseLike<TResult2>) | null,
            ) {
                return p.then(onfulfilled, onrejected);
            },
            unwrap() {
                return p.then((r) => r.unwrap());
            },
            unwrapOr<D>(defaultValue: D) {
                return p.then((r) => r.unwrapOr(defaultValue));
            },
            unwrapOrElse<D>(fn: () => D) {
                return p.then((r) => r.unwrapOrElse(fn));
            },
        };
        return wrapper as AsyncResultWrapper<T, E>;
    }
    return wrap(promise);
}
