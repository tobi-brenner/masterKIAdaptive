import {User} from "@/app/lib/types/users";


export interface CourseSettings {
    language: string,
    periodUnit: string,
    periodLength: number,

}
export interface Course {
    id: number;
    subject: string;
    description: string;
    courseSettings: CourseSettings;
    topics: Topic[];
    assessment: Assessment;
    prof: User;
    learningPaths: any[];
    courseMaterial:  any | null;
    users: any[];
}


export interface Option {

    [key: string]: string;
}

export interface LearningStep{
    id?: number;
    readingMaterial: string;
    explanationText: string;
    tasks: Task[];

}

export interface Task {
    id?: number | null | undefined;
    question: string;
    bloomLevel?: string | undefined;
    correctAnswer?: string;
    taskType?: string;
    options?: Option | null;
    topic?: Topic;
    topicId?: any;
    hint?: string | null;
}

export interface Assessment {
    id: number;
    isInitial: boolean;
    description: string;
    tasks: Task[];
}

export interface AttemptData {
    id: number;
    isInitial: boolean;
    description: string;
    tasks: Task[];
}

export interface Professor {
    id: number;
    name: string;
}

export interface LearningStatistics {
    id: number;
    topicId: number;
    currentBloomLevel: BloomLevel;
    timeSpent: number;
    strengths: string;
    weaknesses: string;
    recommendations: string;
    recordedAt: Date;
}

export interface ExtendedLearningStatistics extends LearningStatistics {
    user: User;
}

export enum BloomLevel {
    NONE = 'NONE',
    REMEMBERING = 'REMEMBERING',
    UNDERSTANDING = 'UNDERSTANDING',
    APPLYING = 'APPLYING',
    ANALYZING = 'ANALYZING',
    EVALUATING = 'EVALUATING',
    CREATING = 'CREATING'
}



export interface UserLearningPath {
    id: string;
    firstName: string | null;
    lastName: string | null;
    email: string | null;
    learningPathId?: string;
}

export interface Topic {
    id?: number | null;
    name: string;
    description: string;
    // maxBloom: string;
    maxBloom: BloomLevel;
    orderNumber: number | null;
    learningGoals: LearningGoal[];
    tasks?: Task[]
}
export interface LearningGoal {
    id?: number | null;
    goal: string;
    description: string;
    maxBloom: string | null;
}



export interface LearningPath {
    id: number;
    course: Course;
    learningSteps: LearningStep[];
    initialAssessmentCompleted: boolean;

}

export interface Feedback {
    id: number;
    content: string;
    createdAt: string;
    feedbackType: 'DIRECT' | 'LLM';
    isIngestedToLLM: boolean;
}

export interface FeedbackData {
    content: string;
    learningPathId: string;
    professorId: string;
    feedbackType: 'DIRECT' | 'LLM';
    isIngestedToLLM: boolean;
}
