'use server';

import {z} from 'zod';
import {sql} from '@vercel/postgres';
import {revalidatePath} from 'next/cache';
import {redirect} from 'next/navigation';
import {signIn} from '@/auth';
import {AuthError} from 'next-auth';
import {BE_BASE_URL} from "@/constants";
import {fetchFromAPI} from "@/app/lib/utils/api";
import {Course} from "@/app/lib/types/courses";

const FormSchema = z.object({
    id: z.string(),
    profUserId: z.string({
        invalid_type_error: 'Please select a prof.',
    }),
    subject: z.coerce
        .string({
            invalid_type_error: 'Please give a Subject name'
        }),
    description: z.string({
        invalid_type_error: 'Please give a valid description.'
    }),
    language: z.string({
        invalid_type_error: 'Please give a valid language'
    }),
    periodLength: z.string({
        invalid_type_error: 'Please give a valid time period.'
    }),
    periodUnit: z.string({
        invalid_type_error: 'Please give a valid time unit.'
    }),
});

const CreateCourse = FormSchema.omit({id: true});
// const UpdateInvoice = FormSchema.omit({date: true, id: true});

// This is temporary
export type State = {
    errors?: {
        profUserId?: string[];
        subject?: string[];
        description?: string[];
        language?: string[];
        periodLength?: string[];
        periodUnit?: string[];
    };
    message?: string | null;
};

export async function updateCourse(courseId: number, courseData: any):Promise<Course> {
    console.log("COURSEDATA::::", courseData)
    const res: Promise<Course> = fetchFromAPI(`/courses/${courseId}`, {
        method: 'PUT',
        body: courseData
    });
    console.log("REES", res);
    return res;

}

export async function createCourse(prevState: State, formData: FormData) {
    console.log(formData, "FORMDATA")
    const validatedFields = CreateCourse.safeParse({
        profUserId: formData.get('profUserId'),
        subject: formData.get('subject'),
        description: formData.get('description'),
        language: formData.get('language'),
        periodLength: formData.get('periodLength'),
        periodUnit: formData.get('periodUnit'),
    });

    // If form validation fails, return errors early. Otherwise, continue.
    if (!validatedFields.success) {
        return {
            errors: validatedFields.error.flatten().fieldErrors,
            message: 'Missing Fields. Failed to Create Course.',
        };
    }
    // Prepare data for insertion into the database
    const {profUserId, subject, description,language,periodUnit, periodLength } = validatedFields.data;

    // Insert data into the database
    try {
        const response = await fetch(`${BE_BASE_URL}/courses`, {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            method: "POST",
            body: JSON.stringify({profUserId, subject, description, language, periodUnit, periodLength})
        })
        console.log("COURSE CREATE RES", response);
        if (!response.ok) {
            throw new Error('Failed to create course');
        }

        const result = await response.json();
        const courseId = result.id;
        console.log("resuilt", result)
        console.log("id", courseId)

    } catch (error) {
        console.log("err", error)

        return {
            message: 'Database Error: Failed to Create Course.',
        };
    }

    // Revalidate the cache for the invoices page and redirect the user.
    revalidatePath('/dashboard/courses');
    redirect(`/dashboard/courses/`);

}




export async function deleteInvoice(id: string) {
    // throw new Error('Failed to Delete Invoice');

    try {
        await sql`DELETE
                  FROM invoices
                  WHERE id = ${id}`;
        revalidatePath('/dashboard/invoices');
        return {message: 'Deleted Invoice'};
    } catch (error) {
        return {message: 'Database Error: Failed to Delete Invoice.'};
    }
}

export async function authenticate(
    prevState: string | undefined,
    formData: FormData,
) {
    try {
        await signIn('credentials', formData);
    } catch (error) {
        if (error instanceof AuthError) {
            switch (error.type) {
                case 'CredentialsSignin':
                    return 'Invalid credentials.';
                default:
                    return 'Something went wrong.';
            }
        }
        throw error;
    }
}
