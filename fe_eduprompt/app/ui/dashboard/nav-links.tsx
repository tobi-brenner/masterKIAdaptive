'use client';

import {
    UserGroupIcon,
    HomeIcon,
    DocumentDuplicateIcon,
} from '@heroicons/react/24/outline';
import Link from 'next/link';
import {usePathname} from 'next/navigation';
import clsx from 'clsx';
import {DocumentIcon} from "@heroicons/react/24/solid";
import {auth} from "@/auth";
import {useSession} from "next-auth/react";
import ThemeSwitch from "@/app/ui/theme-switch";



interface NavLinksProps {
    userId?: string |undefined;
}
export default function NavLinks({userId} : NavLinksProps) {
    const pathname = usePathname();
    const links = [
        {name: 'Home', href: '/dashboard', icon: HomeIcon},
        {name: 'Alle Kurse', href: '/dashboard/courses', icon: DocumentDuplicateIcon},
        {name: 'Benutzer', href: '/dashboard/users', icon: UserGroupIcon},
    ];

    if(userId) {
        links.push({
            name: 'Meine Kurse',
            href: `/learn/courses/${userId}`,
            icon: DocumentIcon
        });

    }


    return (
        <>
            {links.map((link) => {
                const LinkIcon = link.icon;
                return (
                    <Link
                        key={link.name}
                        href={link.href}
                        className={clsx(
                            'flex h-[48px] grow items-center justify-center gap-2 rounded-md bg-gray-50 p-3 text-sm font-medium hover:bg-sky-100 hover:text-blue-600 md:flex-none md:justify-start md:p-2 md:px-3',
                            {
                                'bg-sky-100 text-blue-600': pathname === link.href,
                            },
                        )}
                    >
                        <LinkIcon className="w-6"/>
                        <p className="hidden md:block">{link.name}</p>
                    </Link>
                );
            })}
            {/*<ThemeSwitch />*/}
        </>
    );
}
