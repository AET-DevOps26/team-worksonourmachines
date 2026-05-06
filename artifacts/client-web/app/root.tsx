import { Links, Meta, Outlet, Scripts, ScrollRestoration } from 'react-router';
import './app.css';

export function Layout({ children }: { children: React.ReactNode }) {
    return (
        <html lang="en" suppressHydrationWarning>
            <head>
                <meta charSet="utf-8" />
                <meta content="width=device-width, initial-scale=1" name="viewport" />
                <Meta />
                <Links />
            </head>
            <body className="flex min-h-svh flex-col antialiased" suppressHydrationWarning>
                {children}
                <ScrollRestoration />
                <Scripts />
            </body>
        </html>
    );
}

export default function Root() {
    return <Outlet />;
}
