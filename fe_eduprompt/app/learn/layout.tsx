import SideNav from '@/app/ui/dashboard/sidenav';
import AuthContext from "@/app/dashboard/AuthContext";

export interface AccountLayoutProps {
    children: React.ReactNode;
}

export default function AccountLayout({children}: AccountLayoutProps) {
    return (
        <AuthContext>
            <div className="flex h-screen flex-col md:flex-row md:overflow-hidden">
                <div className="w-full flex-none md:w-64">
                    <SideNav/>
                </div>
                <div className="grow p-6 md:overflow-y-auto md:p-12">{children}</div>
            </div>
        </AuthContext>
    );
}