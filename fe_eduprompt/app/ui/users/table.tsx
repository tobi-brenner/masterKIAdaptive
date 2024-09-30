

"use client";

import React, { useState } from 'react';
import {
    Sheet,
    SheetContent,
    SheetDescription,
    SheetHeader,
    SheetTitle,
    SheetTrigger,
    SheetClose,
} from "@/components/ui/sheet";
import { lusitana } from '@/app/ui/fonts';
import Search from '@/app/ui/search';
import {useRouter} from "next/navigation";
import Link from "next/link";
import SheetItem from "@/app/ui/SheetItem";
import {Course, LearningPath} from "@/app/lib/types/courses";
import {User} from "@/app/lib/definitions";

export default function UsersTable({ users, localSearch=false } : any) {
    const [selectedUser, setSelectedUser] = useState({});
    const router = useRouter();

    return (
        <div className="w-full">
            <h1 className={`${lusitana.className} mb-8 text-xl md:text-2xl`}>
                Users
            </h1>
            {!localSearch &&
                <Search placeholder="Search users..." />
            }
            <div className="mt-6 flow-root">
                <div className="overflow-x-auto">
                    <div className="inline-block min-w-full align-middle">
                        <div className="overflow-hidden rounded-md bg-gray-50 p-2 md:pt-0">
                            <table className="min-w-full rounded-md text-gray-900">
                                <thead className="rounded-md bg-gray-50 text-left text-sm font-normal">
                                <tr>
                                    <th scope="col" className="px-4 py-5 font-medium sm:pl-6">
                                        Name
                                    </th>
                                    <th scope="col" className="px-3 py-5 font-medium">
                                        Email
                                    </th>
                                    <th scope="col" className="px-3 py-5 font-medium">
                                        Student number
                                    </th>
                                    <th scope="col" className="px-3 py-5 font-medium">
                                        Actions
                                    </th>
                                </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-200 text-gray-900">
                                {users.map((user: User) => (
                                    <tr key={user.id} className="group">
                                        <td className="whitespace-nowrap bg-white py-5 pl-4 pr-3 text-sm text-black group-first-of-type:rounded-md group-last-of-type:rounded-md sm:pl-6">
                                            <div className="flex items-center gap-3">
                                                <p>{user.firstName + " " + user.lastName}</p>
                                            </div>
                                        </td>
                                        <td className="whitespace-nowrap bg-white px-4 py-5 text-sm">
                                            {user.email}
                                        </td>
                                        <td className="whitespace-nowrap bg-white px-4 py-5 text-sm">
                                            {user.studentNumber}
                                        </td>
                                        <td className="whitespace-nowrap bg-white px-4 py-5 text-sm">
                                            <button
                                                className="text-blue-600 hover:underline"
                                                onClick={() => router.push(`/dashboard/users/${user.id}`)}
                                            >
                                                View Learning Paths
                                            </button>
                                            <Sheet>
                                                <SheetTrigger asChild>
                                                    <div
                                                        className="text-blue-600 hover:underline cursor-pointer"
                                                        onClick={() => setSelectedUser(user)}
                                                    >
                                                        View Details
                                                    </div>
                                                </SheetTrigger>
                                                <SheetContent side="right">
                                                    <SheetHeader>
                                                        <SheetTitle>{user.firstName} {user.lastName}</SheetTitle>
                                                        <SheetDescription>
                                                            <div>
                                                                <h2 className="text-lg font-semibold">Learning Paths</h2>
                                                                <ul>
                                                                    {user.learningPaths && user.learningPaths.length > 0 ? (
                                                                        user.learningPaths.map((path: LearningPath) => (
                                                                        <SheetItem key={path.id} href={`/dashboard/users/${user.id}/path/${path.id}`}>
                                                                            {path.id}
                                                                        </SheetItem>
                                                                        ))
                                                                    ) : (
                                                                        <li>No learning paths</li>
                                                                    )}
                                                                </ul>
                                                            </div>
                                                            <div>
                                                                <h2 className="mt-4 text-lg font-semibold">Courses</h2>
                                                                <ul>
                                                                    {user.courses && user.courses.length > 0 ? (
                                                                        user.courses.map((course: Course) => (
                                                                            <li key={course.id}>{course.subject}</li>
                                                                        ))
                                                                    ) : (
                                                                        <li>No courses</li>
                                                                    )}
                                                                </ul>
                                                            </div>
                                                        </SheetDescription>
                                                    </SheetHeader>
                                                    <SheetClose asChild>
                                                        <button>Close</button>
                                                    </SheetClose>
                                                </SheetContent>
                                            </Sheet>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
