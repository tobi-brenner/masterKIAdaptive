import '@/app/ui/global.css';
import '@/styles/custom-html.css'
import {inter} from '@/app/ui/fonts';
import {Metadata} from 'next';
import {ThemeProvider} from "next-themes";
import {Toaster} from "@/components/ui/toaster";
import ClientToaster from "@/app/ui/ClientToaster";
import {ClientThemeProvider} from "@/app/ui/ClientThemeProvider";

export const metadata: Metadata = {
    title: {
        template: '%s | Acme Dashboard',
        default: 'Acme Dashboard',
    },
    description: 'The official Next.js Learn Dashboard built with App Router.',
    metadataBase: new URL('https://next-learn-dashboard.vercel.sh'),
};
export default function RootLayout({
                                       children,
                                   }: {
    children: React.ReactNode;
}) {
    return (
        <html lang="en">
        {/*<html lang="en" suppressHydrationWarning>*/}
        <body className={`${inter.className} antialiased`}>
        <ClientThemeProvider>
            {children}
            {/*<Toaster />*/}
            <ClientToaster/>
        </ClientThemeProvider>
        </body>
        </html>
    );
}
