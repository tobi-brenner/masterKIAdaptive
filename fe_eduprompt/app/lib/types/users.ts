import {Task} from "@/app/lib/types/courses";

export interface User {
    id?: string
    firstName?: string | null
    lastName?: string | null
    name?: string | null
    email?: string | null
    role?: string | null
}

export interface SessionUser {
    id?: string
    name?: string | null
    email?: string | null
    role?: string | null
}
export interface UserAttemptData {
    id?: string
    taskId: number;
    userId: string;
    isCorrect: boolean;
    attemptTime: string;
    feedback: string;
    //     TODO: add learningpath etc
}

export interface Attempt {
    id?: string
    taskId?: number;
    userId?: string;
    isCorrect?: boolean;
    attemptTime?: string;
    feedback?: string;
    answer?: string;
    task?: Task;
    assessmentId? : number;
    learningStepId?: number;
}
