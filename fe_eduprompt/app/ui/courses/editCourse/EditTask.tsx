"use client";
import React, { useState } from 'react';
import { Task } from '@/app/lib/types/courses';
import { Button } from '@/app/ui/button';

interface EditTaskProps {
    task: Task;
    updateTask: (taskId: number | any, updatedTask: Task) => void;
    deleteTask: (taskId: number | any) => void;
}

const EditTask: React.FC<EditTaskProps> = ({ task, updateTask, deleteTask }) => {
    const [editTask, setEditTask] = useState<Task>(task);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setEditTask({
            ...editTask,
            [name]: value
        });
    };

    const handleSave = () => {
        updateTask(editTask.id, editTask);
    };

    const handleDelete = () => {
        deleteTask(editTask.id);
    };

    return (
        <div className="task-item mb-4 p-4 rounded-md border border-gray-200 bg-gray-50">
            <div className="mb-2">
                <label htmlFor={`task-question-${task.id}`} className="block text-sm font-medium">
                    Question
                </label>
                <input
                    id={`task-question-${task.id}`}
                    name="question"
                    value={editTask.question}
                    onChange={handleChange}
                    placeholder="Enter question"
                    className="block w-full rounded-md border border-gray-200 py-2 px-3 text-sm outline-none placeholder-gray-500"
                />
            </div>
            <div className="mb-2">
                <label htmlFor={`task-bloomLevel-${task.id}`} className="block text-sm font-medium">
                    Bloom Level
                </label>
                <input
                    id={`task-bloomLevel-${task.id}`}
                    name="bloomLevel"
                    value={editTask.bloomLevel}
                    onChange={handleChange}
                    placeholder="Enter Bloom Level"
                    className="block w-full rounded-md border border-gray-200 py-2 px-3 text-sm outline-none placeholder-gray-500"
                />
            </div>
            <div className="mb-2">
                <label htmlFor={`task-correctAnswer-${task.id}`} className="block text-sm font-medium">
                    Correct Answer
                </label>
                <textarea
                    id={`task-correctAnswer-${task.id}`}
                    name="correctAnswer"
                    value={editTask.correctAnswer}
                    onChange={handleChange}
                    placeholder="Enter correct answer"
                    className="block w-full rounded-md border border-gray-200 py-2 px-3 text-sm outline-none placeholder-gray-500"
                />
            </div>
            {editTask.taskType === 'MULTIPLE_CHOICE' && (
                <div className="mb-2">
                    <label htmlFor={`task-options-${task.id}`} className="block text-sm font-medium">
                        Options
                    </label>
                    {Object.entries(editTask.options || {}).map(([key, value]) => (
                        <input
                            key={key}
                            id={`task-options-${task.id}-${key}`}
                            name={`options-${key}`}
                            value={value}
                            onChange={(e) => {
                                const { name, value } = e.target;
                                setEditTask({
                                    ...editTask,
                                    options: {
                                        ...editTask.options,
                                        [name.split('-')[1]]: value
                                    }
                                });
                            }}
                            placeholder={`Enter option ${key}`}
                            className="block w-full rounded-md border border-gray-200 py-2 px-3 text-sm outline-none placeholder-gray-500"
                        />
                    ))}
                </div>
            )}
            <div className="flex justify-end space-x-2">
                <Button onClick={handleSave}>Save</Button>
                <Button  onClick={handleDelete}>Remove</Button>
            </div>
        </div>
    );
};

export default EditTask;
