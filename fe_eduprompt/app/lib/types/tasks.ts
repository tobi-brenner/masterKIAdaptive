import {Topic} from "@/app/lib/types/courses";

export interface TaskAttemptDTO {
    userId: number  | any;
    taskId: number;
    answer: string | undefined | {};
    assessmentId?: number | string | undefined | null; // Optional
    learningStepId?: number | string | undefined | null; // Optional
    learningPathId?: number;
    isCorrect: boolean;
}

export interface AttemptResponse {
    id: number;
    user: {
        id: number;
        firstName: string;
        lastName: string;
        email: string;
    };
    task: {
        id: number;
        question: string;
        taskType: string;
        topic: Topic;
    };
    attemptTime: string;
    isCorrect: boolean;
    feedback: string;
    answer: string;
}