import {Attempt} from "@/app/lib/types/users";
import {unstable_noStore as noStore} from "next/dist/server/web/spec-extension/unstable-no-store";
import {fetchFromAPI} from "@/app/lib/utils/api";
import {FetchOptions} from "@/app/lib/types/common";

import { Task } from '@/app/lib/types/courses';
import {toast} from "@/components/ui/use-toast";
import {AttemptResponse, TaskAttemptDTO} from "@/app/lib/types/tasks";

export async function fetchUserAttempts(userId: any): Promise<Attempt> {
    noStore();
    const endpoint = `/attempts/user/${userId}`;
    return await fetchFromAPI(endpoint);
}
export async function submitTaskAttempt(data: TaskAttemptDTO): Promise<AttemptResponse> {
    const endpoint = '/attempts';
    console.log("DATA REQ", data);
    const options: FetchOptions = {
        body: data,
    };
    return await fetchFromAPI<AttemptResponse>(endpoint, options);
}




// ---------------------------------

export const updateTask = async (taskId: number | undefined | any, updatedTask: Task) => {
    try {
        await fetchFromAPI(`/tasks/${taskId}`, {
            method: 'PUT',
            body: updatedTask
        });
        toast({
            title: "Task updated successfully",
            description: "Your changes have been saved.",
        });
    } catch (error) {
        console.error('Error updating task:', error);
        toast({
            title: "Error",
            description: "There was an error updating the task.",
            variant: "destructive"
        });
    }
};

export const deleteTask = async (taskId: number) => {
    try {
        await fetchFromAPI(`/tasks/${taskId}`, {
            method: 'DELETE'
        });
        toast({
            title: "Task deleted successfully",
            description: "The task has been deleted.",
        });
    } catch (error) {
        console.error('Error deleting task:', error);
        toast({
            title: "Error",
            description: "There was an error deleting the task.",
            variant: "destructive"
        });
    }
};

export const createTask = async (task: Task) => {
    try {
        await fetchFromAPI(`/tasks`, {
            method: 'POST',
            body: task
        });
        toast({
            title: "Task created successfully",
            description: "The new task has been created.",
        });
    } catch (error) {
        console.error('Error creating task:', error);
        toast({
            title: "Error",
            description: "There was an error creating the task.",
            variant: "destructive"
        });
    }
};
