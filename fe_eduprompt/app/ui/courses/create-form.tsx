"use client"
import { CustomerField } from '@/app/lib/definitions';
import { headers } from 'next/headers'
import Link from 'next/link';
import {
    CheckIcon,
    ClockIcon,
    CurrencyDollarIcon,
    UserCircleIcon,
} from '@heroicons/react/24/outline';
import { Button } from '@/app/ui/button';
import { createInvoice } from '@/app/lib/actions';
import { useFormState } from 'react-dom';
import {createCourse} from "@/app/ui/courses/actions";
import {auth} from "@/auth";
import {useSession} from "next-auth/react";
import {Toaster} from "@/components/ui/toaster";

export default function Form({ profUserId }: { profUserId: string  }) {
    const initialState = { message: null, errors: {} };
    const [state, dispatch] = useFormState(createCourse, initialState);

    console.log(profUserId, "profid");
    return  (
        <form action={dispatch}>
            <div className="rounded-md bg-gray-50 p-4 md:p-6">
                {/* Customer Name */}
                <div className="mb-4 hidden">
                    <input type="text" id="profUserId" name="profUserId" value={profUserId}/>
                </div>

                {/* Course Subject */}
                <div className="mb-4">
                    <label htmlFor="subject" className="mb-2 block text-sm font-medium">
                        Enter subject of course
                    </label>
                    <div className="relative mt-2 rounded-md">
                        <div className="relative">
                            <input
                                id="subject"
                                name="subject"
                                type="text"
                                placeholder="Enter subject of course"
                                className="peer block w-full rounded-md border border-gray-200 py-2 pl-10 text-sm outline-2 placeholder:text-gray-500"
                                aria-describedby="subject-error"
                            />
                            {/*<CurrencyDollarIcon className="pointer-events-none absolute left-3 top-1/2 h-[18px] w-[18px] -translate-y-1/2 text-gray-500 peer-focus:text-gray-900" />*/}
                        </div>
                    </div>

                    <div id="subject-error" aria-live="polite" aria-atomic="true">
                        {state.errors?.subject &&
                            state.errors.subject.map((error: string) => (
                                <p className="mt-2 text-sm text-red-500" key={error}>
                                    {error}
                                </p>
                            ))}
                    </div>
                </div>


                {/* Course Description */}
                <div className="mb-4">
                    <label htmlFor="description" className="mb-2 block text-sm font-medium">
                        Enter the description of course
                    </label>
                    <div className="relative mt-2 rounded-md">
                        <div className="relative">
                            <input
                                id="description"
                                name="description"
                                type="text"
                                placeholder="Enter a description of course"
                                className="peer block w-full rounded-md border border-gray-200 py-2 pl-10 text-sm outline-2 placeholder:text-gray-500"
                                aria-describedby="description-error"
                            />
                            {/*<CurrencyDollarIcon*/}
                            {/*    className="pointer-events-none absolute left-3 top-1/2 h-[18px] w-[18px] -translate-y-1/2 text-gray-500 peer-focus:text-gray-900"/>*/}
                        </div>
                    </div>

                    <div id="description-error" aria-live="polite" aria-atomic="true">
                        {state.errors?.description &&
                            state.errors.description.map((error: string) => (
                                <p className="mt-2 text-sm text-red-500" key={error}>
                                    {error}
                                </p>
                            ))}
                    </div>
                </div>
                <div className="mb-4">
                    <label htmlFor="language" className="mb-2 block text-sm font-medium text-gray-600">
                        Language
                    </label>
                    <input
                        id="language"
                        name="language"
                        type="text"
                        // value={formData.language}
                        // onChange={handleChange}
                        placeholder="Enter language of course"
                        className="peer block w-full rounded-md border border-gray-200 py-2 pl-10 text-sm outline-2 placeholder:text-gray-500"
                        aria-describedby="description-error"
                    />
                </div>
                <div id="description-error" aria-live="polite" aria-atomic="true">
                    {state.errors?.language &&
                        state.errors.language.map((error: string) => (
                            <p className="mt-2 text-sm text-red-500" key={error}>
                                {error}
                            </p>
                        ))}
                </div>

                {/* Period Fields */}
                <div className="mb-4">
                    <label htmlFor="periodLength" className="mb-2 block text-sm font-medium text-gray-600">
                        Period
                    </label>
                    <div className="flex space-x-4">
                        <input
                            id="periodLength"
                            name="periodLength"
                            type="number"
                            placeholder="Enter duration"
                            className="block w-full rounded-md border border-gray-300 p-2"
                        />
                        <select
                            id="periodUnit"
                            name="periodUnit"
                            className="block w-full rounded-md border border-gray-300 p-2"
                        >
                            <option value="WEEKS">Weeks</option>
                            <option value="MONTHS">Months</option>
                            <option value="YEARS">Years</option>
                        </select>
                    </div>
                </div>

                <div aria-live="polite" aria-atomic="true">
                    {state.message ? (
                        <p className="mt-2 text-sm text-red-500">{state.message}</p>
                    ) : null}
                </div>
            </div>
            <div className="mt-6 flex justify-end gap-4">
                <Link
                    href="/dashboard/courses"
                    className="flex h-10 items-center rounded-lg bg-gray-100 px-4 text-sm font-medium text-gray-600 transition-colors hover:bg-gray-200"
                >
                    Cancel
                </Link>
                <Button type="submit">Create Course</Button>
            </div>
        </form>
    );

}
