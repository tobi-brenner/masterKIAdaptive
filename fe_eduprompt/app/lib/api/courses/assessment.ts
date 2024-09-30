
import {unstable_noStore as noStore} from 'next/cache';
import {BE_BASE_URL} from "@/constants";
import {Assessment, Course} from "@/app/lib/types/courses";
import {useRouter} from "next/navigation";
import {fetchFromAPI} from "@/app/lib/utils/api";

const ASSESSMENT_BASE = `${BE_BASE_URL}/assessment`

export async function enrollUserToCourse(currentUserId: string | undefined, courseId: number) {
    noStore();
    console.log(currentUserId)
    console.log(courseId)
    try {
        let userId =-1;
        if(currentUserId) {
            userId = parseInt(currentUserId);
        }
        const response = await fetch(`${ASSESSMENT_BASE}/{}`, {
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

export const fetchAssessmentByCourseId = async (courseId: string | null):Promise<Assessment> => {
    const endpoint = `/courses/${courseId}/assessment`;
    return await fetchFromAPI(endpoint);
};