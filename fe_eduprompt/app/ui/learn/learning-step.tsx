
'use client'
import {useEffect, useState} from 'react';
import {useSession} from 'next-auth/react';
import {LearningPath, LearningStep} from '@/app/lib/types/courses';
import {Attempt} from "@/app/lib/types/users";
import TaskCard from '@/app/ui/learn/task-card';
import {submitTaskAttempt} from '@/app/lib/api/courses/task';
import {LearningStepSkeleton} from '@/app/ui/skeletons';
import {generateNextStep} from "@/app/lib/api/learningpath/learningpath";
// import { fetchUserAttemptsForLearningStep } from '@/app/lib/api/attempts';
import {fetchUserAttemptsForAssessment, fetchUserAttemptsForLearningStep} from "@/app/lib/api/courses/learningpath";

interface LearningStepProps {
    learningStep: LearningStep;
    learningPath: LearningPath;
    userId: string | undefined;
}

export default function LearningStep({learningStep, learningPath, userId}: LearningStepProps) {
    const {id, readingMaterial, explanationText, tasks} = learningStep;
    const {data: session, status} = useSession();
    const [allTasksCompleted, setAllTasksCompleted] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isGenerating, setIsGenerating] = useState(false);
    const [attempts, setAttempts] = useState<Attempt[]>([]);

    useEffect(() => {
        const fetchAttempts = async () => {
            const fetchedAttempts = await fetchUserAttemptsForLearningStep(userId, id);
            setAttempts(fetchedAttempts);
        };

        fetchAttempts();
    }, [id, userId]);

    useEffect(() => {
        checkCompletion();
    }, [attempts]);

    const handleGenerateNextStep = async () => {
        setIsGenerating(true);
        await generateNextStep(learningPath.id, session!.user.id);
        setIsGenerating(false);
        window.location.reload();
    };

    const checkCompletion = () => {
        const completed = tasks.every(task => getAttemptForTask(task.id!, null, id!));
        setAllTasksCompleted(completed);
    };

    const getAttemptForTask = (taskId: number, currentAssessmentId: number | null, currentLearningStepId: number | null) => {
        return attempts.find(attempt =>
            attempt.task!.id === taskId &&
            ((attempt.assessmentId && attempt.assessmentId === currentAssessmentId) ||
                (attempt.learningStepId && attempt.learningStepId === currentLearningStepId))
        );
    };

    const handleTaskCompletion = (newAttempt: Attempt) => {
        setAttempts(prevAttempts => [...prevAttempts, newAttempt]);
    };

    if (status === 'loading') {
        return <LearningStepSkeleton/>;
    }

    if (!session || !session.user) {
        return <div>Please log in to see your learning step.</div>;
    }

    return (
        <div className="">
            <div className="mb-4 p-4 bg-gray-50 rounded-lg shadow-md">
                <div className={'custom-html'} dangerouslySetInnerHTML={{__html: explanationText}}/>
            </div>
            <div className="mb-4 p-4 bg-gray-50 rounded-lg shadow-md">
                <div className={'custom-html'} dangerouslySetInnerHTML={{__html: readingMaterial}}/>
            </div>
            {isSubmitting ? (
                <LearningStepSkeleton/>
            ) : (
                <div>
                    {tasks.map((task, i) => (
                        <TaskCard
                            key={i}
                            task={task}
                            attempt={getAttemptForTask(task.id!, null, id!)}
                            user={session!.user}
                            learningStep={learningStep}
                            learningPath={learningPath}
                            showFeedback={true}
                            onTaskCompleted={handleTaskCompletion}
                        />
                    ))}
                </div>
            )}
            <button
                onClick={handleGenerateNextStep}
                disabled={!allTasksCompleted || isGenerating}
                className={`mt-4 p-2 rounded ${allTasksCompleted ? 'bg-blue-500 text-white' : 'bg-gray-300 text-gray-500 cursor-not-allowed'}`}
            >
                {isGenerating ? (
                    <div className="flex items-center">
                        <svg className="animate-spin h-5 w-5 mr-2 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                        </svg>
                        Loading...
                    </div>
                ) : (
                    "Nächster Schritt"
                )}

            </button>
        </div>
    );
}
