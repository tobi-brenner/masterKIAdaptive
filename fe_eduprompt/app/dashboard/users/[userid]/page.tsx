import Breadcrumbs from '@/app/ui/invoices/breadcrumbs';
import {fetchUserLearningPaths} from "@/app/lib/api/courses/course";
import Link from "next/link";
import React from "react";
import LinkButton from "@/app/ui/LinkButton";

export const metadata = {
    title: 'User Learning Paths',
};

export default async function Page({ params }: { params: { userid: string } }) {
    const { userid } = params;
    const learningPaths = await fetchUserLearningPaths(userid);
    console.log(learningPaths)

    return (
        <main>
            <Breadcrumbs
                breadcrumbs={[
                    {label: 'Users', href: '/users'},
                    {label: 'Learning Paths', href: `/users/${userid}`, active: true},
                ]}
            />
            <h1 className="text-2xl font-bold mb-6">Learning Paths</h1>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {learningPaths.map((path) => (
                    <div key={path.id} className="bg-white rounded-lg shadow-md border border-gray-200 p-6 mb-8">
                        <h2 className="text-xl font-semibold mb-2">Learning Path for: {path.id}</h2>
                        <LinkButton href={`/dashboard/users/${userid}/path/${path.id}`}>View Details</LinkButton>
                    </div>
                ))}
            </div>
        </main>
    );
}
