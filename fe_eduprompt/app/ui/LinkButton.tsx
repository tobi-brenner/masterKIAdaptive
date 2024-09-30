'use client';

import Link from 'next/link';
import { ReactNode } from 'react';

interface LinkButtonProps {
    href: string;
    children: ReactNode;
}

export default function LinkButton({ href, children }: LinkButtonProps) {
    return (
        <Link href={href}>
            <div className="inline-block px-4 py-2 text-white bg-blue-600 rounded hover:bg-blue-700">
                {children}
            </div>
        </Link>
    );
}
