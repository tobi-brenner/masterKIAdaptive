


"use client";

import React, {useEffect, useState} from 'react';
import {Button} from '@/app/ui/button';
import {Assessment, Task, Topic} from '@/app/lib/types/courses';
import EditTask from "@/app/ui/courses/editCourse/EditTask";
import TaskSelectionModal from "@/app/ui/courses/editCourse/TaskSelectionModal";

interface InitialAssessmentSectionProps {
    assessment: Assessment | null;
    createInitialAssessment: () => void;

    topics: Topic[]
    updateTaskInAssessment: (taskId: number, assessmentId: number) => void;
    removeTaskFromAssessment: (taskId: number) => void;
}

const InitialAssessmentSection: React.FC<InitialAssessmentSectionProps> = ({
                                                                               assessment,
                                                                               createInitialAssessment,
                                                                               topics,
                                                                               updateTaskInAssessment,
                                                                               removeTaskFromAssessment

                                                                           }) => {

    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleOpenModal = () => {
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
    };

    const handleTaskSelection = (taskId: number) => {
        if (assessment?.id) {
            updateTaskInAssessment(taskId, assessment.id); // Add task to the assessment
        }
    };

    return (
        <div>
            {assessment ? (
                <div className="assessment-section mb-6">
                    {assessment.tasks.map((task, index) => (

                        <EditTask
                            key={index}
                            task={task}
                            updateTask={(taskId, updatedTask) => updateTaskInAssessment(taskId, assessment.id)}
                            deleteTask={removeTaskFromAssessment} // Adjusted from delete to remove from assessment
                        />
                    ))}
                    {/* Button to open the modal */}
                    <Button onClick={handleOpenModal} className="mt-4">
                        Add Task to Assessment
                    </Button>

                    {/* Task selection modal */}
                    <TaskSelectionModal
                        topics={topics}
                        isOpen={isModalOpen}
                        onClose={handleCloseModal}
                        onSelectTask={handleTaskSelection}
                        assessmentTasks={assessment.tasks}
                    />
                </div>
            ) : (
                <Button onClick={createInitialAssessment}>Create Initial Assessment</Button>
            )}
        </div>
    );
}

export default InitialAssessmentSection;
