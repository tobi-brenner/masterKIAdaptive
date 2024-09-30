import {unstable_noStore as noStore} from 'next/cache';
import {BE_BASE_URL} from "@/constants";
import {fetchFromAPI} from "@/app/lib/utils/api";
import {LearningPath} from "@/app/lib/types/courses";
import {User} from "@/app/lib/types/users";

const COURSE_BASE = `${BE_BASE_URL}/courses`



export const fetchUsers = async (query = ""): Promise<User[]> => {
    noStore();
    try {
        const res = await fetch(`${BE_BASE_URL}/users?query=${encodeURIComponent(query)}`);
        const data = await res.json();
        console.log("FETCHJUSER DATA ",data);
        return data;
    } catch (err) {
        console.log(err);
    }
    return [];
};
export const fetchEnrolledUsers = async (courseId: string):Promise<User[]> => {
    noStore();
    try {
        const response = await fetch(`${BE_BASE_URL}/users/enrolled?courseId=${courseId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch enrolled users');
        }
        return response.json();
    } catch (e) {
        console.log(e);
    }
    return [];
}


export async function getCourseAssessment(currentUserId: string | undefined, courseId: number) {
    noStore();
    try {
        let userId =-1;
        if(currentUserId) {
            userId = parseInt(currentUserId);
        }
        const response = await fetch(`${COURSE_BASE}/${courseId}/assessment`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ userId, courseId })
        });
        if (!response.ok) {
            const errorText = await response.text();  // Expecting text in case of error
            throw new Error(`Failed to enroll user: ${errorText}`);
        }

        // Check if there is any content to parse
        if (response.headers.get("content-type")?.includes("application/json")) {
            const result = await response.json();  // Only parse if JSON content is expected
            console.log('Enrollment successful:', result);
        } else {
            console.log('Enrollment successful, no details returned.');
        }
        return true;
    } catch (error) {
        console.error('Error enrolling user:', error);
        return false;
    }

}
export async function enrollUserToCourse(currentUserId: string | undefined, courseId: number) {
    noStore();
    const endpoint = `/users/enroll`;
    const body = { userId: parseInt(currentUserId!) || -1, courseId };
    console.log("BODY", body);
    return await fetchFromAPI(endpoint, { method: 'POST', body });
}

export const fetchCourses = async (userId: string | undefined) => {
    noStore();
    // const endpoint = `/users/${userId}/courses`;
    const endpoint = `/courses`;
    return await fetchFromAPI(endpoint);
}

export const fetchUser = async (userId: string | undefined): Promise<User> => {
    noStore();
    const endpoint = `/users/${userId}`;
    return await fetchFromAPI(endpoint);
}
