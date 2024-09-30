import {unstable_noStore as noStore} from 'next/cache';
import {fetchFromAPI} from "@/app/lib/utils/api";
import {Feedback, FeedbackData, LearningPath, LearningStatistics} from "@/app/lib/types/courses";
import {Attempt, UserAttemptData} from "@/app/lib/types/users";

export async function fetchLearningPathById(pathId: any): Promise<LearningPath> {
    noStore();
    const endpoint = `/learningpath/${pathId}`;
    return await fetchFromAPI(endpoint);
}

export const fetchLearningStatistics = async (
    userId: string,
    learningPathId: string,
    courseId: string
): Promise<LearningStatistics[]> => {
    const endpoint = `/learning-statistics/${userId}/learning-path/${learningPathId}/course/${courseId}`;
    return await fetchFromAPI(endpoint);
};

export const fetchFeedbacks = async (
    learningPathId: string,
): Promise<Feedback[]> => {
    const endpoint = `/feedback/list/${learningPathId}`;
    return await fetchFromAPI(endpoint);
};
export const createFeedback = async (feedbackData: FeedbackData): Promise<void> => {
    noStore();
    const endpoint = '/feedback/create';
    const body = feedbackData;
    console.log("BODY", body);
    return await fetchFromAPI(endpoint, { method: 'POST', body });
};

export async function fetchUserAttempts(userId: any): Promise<Attempt[]> {
    noStore();
    const endpoint = `/attempts/user/${userId}`;
    return await fetchFromAPI(endpoint);
}
export async function fetchUserAttemptsForLearningStep(userId: any, learningstepId: any): Promise<Attempt[]> {
    noStore();
    const endpoint = `/attempts/user/${userId}/learningstep/${learningstepId}`;
    return await fetchFromAPI(endpoint);
}
export async function fetchUserAttemptsForAssessment(userId: any, assessmentId: any): Promise<Attempt[]> {
    noStore();
    const endpoint = `/attempts/user/${userId}/assessment/${assessmentId}`;
    return await fetchFromAPI(endpoint);
}
