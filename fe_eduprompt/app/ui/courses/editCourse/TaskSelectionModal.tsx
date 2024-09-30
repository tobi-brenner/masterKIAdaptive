

import React, { useState, useEffect } from 'react';
import { Task, Topic } from '@/app/lib/types/courses';
import { Button } from '@/app/ui/button';

interface TaskSelectionModalProps {
    topics: Topic[];
    assessmentTasks: Task[];  // New prop to pass tasks already in the assessment
    isOpen: boolean;
    onClose: () => void;
    onSelectTask: (taskId: number) => void;
}

const TaskSelectionModal: React.FC<TaskSelectionModalProps> = ({ topics, assessmentTasks, isOpen, onClose, onSelectTask }) => {
    const [selectedTopicId, setSelectedTopicId] = useState<number | null>(null);
    const [availableTasks, setAvailableTasks] = useState<Task[]>([]);
    const [selectedTaskId, setSelectedTaskId] = useState<number | null>(null);

    // When a topic is selected, fetch tasks for that topic
    useEffect(() => {
        if (selectedTopicId !== null) {
            const selectedTopic = topics.find(topic => topic.id === selectedTopicId);
            if (selectedTopic) {
                setAvailableTasks(selectedTopic.tasks!);
                setSelectedTaskId(null); // Reset the selected task when switching topics
            }
        }
    }, [selectedTopicId, topics]);

    const handleAddTask = () => {
        if (selectedTaskId) {
            onSelectTask(selectedTaskId);
            onClose(); // Close the modal after selecting
        }
    };

    const isTaskInAssessment = (taskId: number) => {
        return assessmentTasks.some(task => task.id === taskId);
    };

    if (!isOpen) return null; // Don't render modal if it's not open

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white p-8 rounded-lg shadow-lg max-w-2xl w-full">
                <h2 className="text-xl font-bold mb-6">Select a Task</h2>

                {/* Topic Selection */}
                <div className="mb-6">
                    <label htmlFor="topic-select" className="block text-sm font-medium mb-2">Select Topic</label>
                    <select
                        id="topic-select"
                        value={selectedTopicId || ''}
                        onChange={(e) => setSelectedTopicId(Number(e.target.value))}
                        className="block w-full rounded-lg border border-gray-300 py-2 px-3 text-sm"
                    >
                        <option value="">Select a topic</option>
                        {topics.map((topic) => (
                            <option key={topic.id} value={topic.id!}>{topic.name}</option>
                        ))}
                    </select>
                </div>

                {/* Task Selection */}
                {availableTasks.length > 0 ? (
                    <div className="mb-6">
                        <label htmlFor="task-select" className="block text-sm font-medium mb-2">Select Task</label>
                        <select
                            id="task-select"
                            value={selectedTaskId || ''}
                            onChange={(e) => setSelectedTaskId(Number(e.target.value))}
                            className="block w-full rounded-lg border border-gray-300 py-2 px-3 text-sm"
                        >
                            <option value="">Select a task</option>
                            {availableTasks.map((task) => (
                                <option
                                    key={task.id}
                                    value={task.id!}
                                    disabled={isTaskInAssessment(task.id!)}  // Disable if already in assessment
                                >
                                    {task.question} {isTaskInAssessment(task.id!) ? '(Already in Assessment)' : ''}
                                </option>
                            ))}
                        </select>
                    </div>
                ) : selectedTopicId && (
                    <p className="text-sm text-gray-500">No tasks available for this topic.</p>
                )}

                {/* Action Buttons */}
                <div className="flex justify-end space-x-4 mt-6">
                    <Button onClick={onClose} className="bg-gray-300 hover:bg-gray-400">Cancel</Button>
                    <Button
                        onClick={handleAddTask}
                        disabled={!selectedTaskId || isTaskInAssessment(selectedTaskId)}  // Disable if no task is selected or if selected task is in assessment
                        className="bg-blue-500 hover:bg-blue-600 text-white"
                    >
                        Add Task
                    </Button>
                </div>
            </div>
        </div>
    );
};

export default TaskSelectionModal;
