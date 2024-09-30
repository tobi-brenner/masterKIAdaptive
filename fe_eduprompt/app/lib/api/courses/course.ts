import {unstable_noStore as noStore} from 'next/cache';
import {BE_BASE_URL} from "@/constants";
import {fetchFromAPI} from "@/app/lib/utils/api";
import {Course, LearningPath} from "@/app/lib/types/courses";
import {User} from "@/app/lib/types/users";

const COURSE_BASE = `${BE_BASE_URL}/courses`


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

// Fetches learning paths for a specific user
export const   fetchLearningPathsByUserId = async (userId: string): Promise<LearningPath[]> => {
    noStore();
    // const endpoint = `/users/${userId}/courses`;
    const endpoint = `/learningpath/user/${userId}`;
    return await fetchFromAPI(endpoint);
}
export const fetchCoursesById = async (courseId: string | undefined):Promise<Course> => {
    noStore();
    // const endpoint = `/users/${userId}/courses`;
    const endpoint = `/courses/${courseId}`;
    return await fetchFromAPI(endpoint);
}

export const fetchUsersOfCourse = async (courseId: string | undefined):Promise<User[]> => {
    noStore();
    // const endpoint = `/users/${userId}/courses`;
    const endpoint = `/users/course/${courseId}/enrolled-users`;
    return await fetchFromAPI(endpoint);
}


export const fetchUserLearningPaths = async (userId: string | undefined):Promise<LearningPath[]> => {
    noStore();
    // const endpoint = `/users/${userId}/courses`;
    const endpoint = `/users/${userId}/learningpaths`;
    return await fetchFromAPI(endpoint);
}
export const fetchLearningPath = async (learningpathid: string | undefined):Promise<LearningPath> => {
    noStore();
    console.log("INSIDE GRABBING single learingpath")
    // const endpoint = `/users/${userId}/courses`;
    const endpoint = `/learningpath/${learningpathid}`;
    return await fetchFromAPI(endpoint);
}

export const fetchCourses = async (userId: string | undefined):Promise<Course[]> => {
    noStore();
    // const endpoint = `/users/${userId}/courses`;
    const endpoint = `/courses`;
    return await fetchFromAPI(endpoint);


}