"use client"
import { useState } from 'react';
import {enrollUserToCourse} from "@/app/lib/api/courses/course";
import {useRouter} from "next/navigation";

const EnrollButton = ({currentUserId, courseId} : {currentUserId: any, courseId: any}) => {
    const router = useRouter();

    const handleEnrollment = async (userId: string | undefined, courseId: number) => {
        const result = await enrollUserToCourse(userId, courseId);
        if (result) {
            router.push('/learn/courses');
        } else {
            console.log('Failed to enroll user');
        }
    };

    return (
        <div>
            <button
                onClick={() => handleEnrollment(currentUserId, courseId)}
                className="bg-blue-500 text-white font-bold py-2 px-4 rounded hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 mt-4"
            >
                Einschreiben
            </button>
        </div>
    );
};

export default EnrollButton;