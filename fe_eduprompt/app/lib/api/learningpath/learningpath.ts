import {unstable_noStore as noStore} from 'next/cache';
import {BE_BASE_URL} from "@/constants";
import {fetchAssessmentByCourseId} from "@/app/lib/api/courses/assessment";
import {fetchLearningPathById, fetchLearningStatistics} from "@/app/lib/api/courses/learningpath";
import {Assessment, LearningPath, LearningStatistics} from "@/app/lib/types/courses";
import {fetchUser} from "@/app/lib/api/user/user";
import {User} from "@/app/lib/types/users";
import {fetchFromAPI} from "@/app/lib/utils/api";

export const fetchLearningPathData = async (pathId: string, courseId: string):Promise<{ initialAssessment: Assessment, learningPath: LearningPath }> => {
    noStore();
    try {
        const initialAssessmentPromise = await fetchAssessmentByCourseId(courseId);
        const learningPathPromise = await fetchLearningPathById(pathId);
        // const userDataPromise = await fetchUser(userId);

        const data = await Promise.all([
            initialAssessmentPromise,
            learningPathPromise,
            // userDataPromise
        ]);

        const initialAssessment = data[0];
        const learningPath = data[1];
        // const user = data[2];

        return {
            initialAssessment,
            learningPath,
            // user
        }
    } catch (error) {
        console.error('Database Error:', error);
        throw new Error('Failed to fetch card data.');
    }
}



export const fetchLearningPaths = async (userId: string | undefined) => {
    noStore();
    let uri = `${BE_BASE_URL}/learningpath/user/${userId}`

    try {
        const response = await fetch(uri, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        });

        if (!response.ok) {
            console.error('Course fetching failed:', response.statusText);
            return null; // Return null on failed network request
        }

        const paths: any = await response.json();
        return paths || null;
    } catch (error) {
        console.error('Failed to log in user:', error);
        return null; // Return null on exception
    }

}

export const generateFirstStep = async (
    learningpathId: string | undefined | number,
    userId: string | undefined,
    assessmentId: string | undefined | number,
    skip: boolean
) => {
    noStore();

    // Add the 'skip' query parameter to the URL
    let uri = `/learningpath/firststep/${learningpathId}?skip=${skip}`;

    // Make the request with the modified URL
    const res = await fetchFromAPI(uri, { method: "POST" });
    console.log("FIRST STEP GEN:", res);
    return res;
};


export const generateNextStep = async (learningpathId: string | undefined | number, userId: string | undefined) => {
    noStore();
    let uri = `/learningpath/nextstep/${learningpathId}`;
    const res = await fetchFromAPI(uri, {method: "POST"});
    console.log("NEXT STEP GEN:", res);
    return res;
}
