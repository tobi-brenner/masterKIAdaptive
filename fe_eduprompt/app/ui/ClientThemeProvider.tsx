'use client';

import { ThemeProvider } from "next-themes";

export function ClientThemeProvider({ children }: { children: React.ReactNode }) {
    return <ThemeProvider defaultTheme={"light"}>{children}</ThemeProvider>;
}
