'use client'
import {Assessment, AttemptData, LearningPath} from "@/app/lib/types/courses";
import TaskCard from "@/app/ui/learn/task-card";
import {useSession} from "next-auth/react";
import {Attempt, UserAttemptData} from "@/app/lib/types/users";
import {useEffect, useState} from "react";
import {generateFirstStep} from "@/app/lib/api/learningpath/learningpath";
import {InitialAssessmentSkeleton} from "@/app/ui/skeletons";
import {fetchUserAttemptsForAssessment, fetchUserAttemptsForLearningStep} from "@/app/lib/api/courses/learningpath";

interface InitialAssessmentProps {
    assessment: Assessment;
    attemptData: Attempt[];
    learningPath: LearningPath;
    userId: string | undefined;
}

export default function InitialAssessment({assessment, attemptData, learningPath, userId}: InitialAssessmentProps) {
    const {id, isInitial, description, tasks} = assessment;
    const {data: session, status} = useSession();
    const [allTasksCompleted, setAllTasksCompleted] = useState(false);
    const [isGenerating, setIsGenerating] = useState(false);
    const [attempts, setAttempts] = useState<Attempt[]>([]);

    const checkCompletion = () => {
        const completed = tasks.every(task => getAttemptForTask(task.id!, assessment.id, null));
        setAllTasksCompleted(completed);
    };

    useEffect(() => {
        const fetchAttempts = async () => {
            const fetchedAttempts = await fetchUserAttemptsForAssessment(userId, id);
            setAttempts(fetchedAttempts);
        };

        fetchAttempts();
    }, [id, userId]);

    useEffect(() => {
        checkCompletion();
    }, [attempts]);

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


    const handleGenerateFirstStep = async (skip = false) => {
        try {
            setIsGenerating(true);
            console.log("GEN FIRST STEP: ", skip)
            await generateFirstStep(learningPath.id, session!.user.id, assessment.id.toString(10), skip);
            window.location.reload(); // reload on success
        } catch (error) {
            console.error("Error generating the first step:", error);
            window.location.reload(); // reload on failure
        } finally {
            setIsGenerating(false);
        }
    };







    if (status === 'loading') {
        return <InitialAssessmentSkeleton/>
    }

    if (!session || !session.user) {
        return <div>Please log in to see your assessment.</div>;
    }

    return (
        <div className="">
            <div className="p-2 rounded mb-4">
                <button
                    onClick={() => handleGenerateFirstStep(true)} // Trigger skip behavior
                    className="bg-red-400 text-white p-2 rounded mb-4"
                    title="Falls du kein Vorwissen in dem Thema hast, kannst du den Einstiegstest auch überspringen"
                >
                    Überspringen
                </button>
                <p>Falls du kein Vorwissen in dem Thema hast, kannst du den Einstiegstest auch überspringen</p>
            </div>
            {/*<h2 className="text-2xl font-bold mb-4">Initial Assessment</h2>*/}
            {/*<p className="mb-4">Description: {description}</p>*/}
            {isGenerating ? (
                <InitialAssessmentSkeleton/>
            ) : (
                <div>
                    {tasks.map(task => (
                        <TaskCard
                            key={task.id}
                            task={task}
                            attempt={getAttemptForTask(task.id!, assessment.id, null)}
                            user={session.user}
                            assessment={assessment}
                            learningPath={learningPath}
                            showFeedback={false}
                            // onTaskCompleted={checkCompletion}
                            onTaskCompleted={handleTaskCompletion}
                        />
                    ))}
                </div>
            )}
            <button
                onClick={() => handleGenerateFirstStep(false)}
                disabled={!allTasksCompleted || learningPath.initialAssessmentCompleted}
                className={`mt-4 p-2 rounded ${allTasksCompleted && !learningPath.initialAssessmentCompleted ? 'bg-blue-500 text-white' : 'bg-gray-300 text-gray-500 cursor-not-allowed'}`}
            >
                Next
            </button>
        </div>
    )
}
