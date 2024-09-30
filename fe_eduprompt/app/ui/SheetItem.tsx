'use client';

import Link from 'next/link';
import { ReactNode } from 'react';

interface SheetItemProps {
    href: string;
    children: ReactNode;
}

export default function SheetItem({ href, children }: SheetItemProps) {
    return (
        <Link href={href}>
            <div className="block w-full p-4 mb-2 text-black bg-white rounded hover:bg-gray-100">
                {children}
            </div>
        </Link>
    );
}
