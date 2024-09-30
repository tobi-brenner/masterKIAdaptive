import Breadcrumbs from '@/app/ui/invoices/breadcrumbs';
import { Metadata } from 'next';
import {auth} from "@/auth";
import Form from "@/app/ui/courses/create-form";
import {fetchCoursesById, fetchUsersOfCourse, fetchLearningPathsByUserId} from "@/app/lib/api/courses/course";
import CourseDashboard from "@/app/ui/courses/dashboard/courseDashboard";
import {UserLearningPath} from "@/app/lib/types/courses";
import UsersTable from "@/app/ui/users/table";

export const metadata: Metadata = {
    title: 'CourseDashboard',
};





export default async function Page({ params }: { params: { id: string } }) {
    const courseId = params.id;
    const session = await auth();
    const course = await fetchCoursesById(courseId);
    const users = await fetchUsersOfCourse(courseId);


    const userLearningPaths: UserLearningPath[] = await Promise.all(users.map(async (user) => {
        const learningPaths = await fetchLearningPathsByUserId(user.id!);
        const learningPath = learningPaths.find(lp => lp.course.id.toString() === courseId);
        return {
            id: user.id!,
            firstName: user.firstName ?? "",
            lastName: user.lastName ?? "",
            email: user.email ?? "",
            learningPathId: learningPath?.id.toString(),

        };
    }));
    console.log("USERLEAN", userLearningPaths)
    console.log(userLearningPaths, "USER LEARNING PATHS");
    console.log(users, "USERS")
    console.log(course)

    if(session?.user.role == "teacher" && session?.user.id) {
        // @ts-ignore
        return (
            <main>
                <Breadcrumbs
                    breadcrumbs={[
                        {label: 'Courses', href: '/dashboard/courses'},
                        {
                            label: 'Edit Course',
                            href: `/dashboard/courses/${courseId}`,
                            active: true,
                        },
                    ]}
                />
                <div>
                    {/*<Suspense fallback={<CardsSkeleton />}>*/}
                    <CourseDashboard course={course} userLearningPaths={userLearningPaths} />
                    <UsersTable users={users} localSearch={true} />
                    {/*</Suspense>*/}
                </div>
            </main>
        )
    } else if(session?.user.id) {
        return (
            <main>
                <Breadcrumbs
                    breadcrumbs={[
                        {label: 'Courses', href: '/dashboard/'},
                        {
                            label: 'Edit Course',
                            href: `/dashboard/courses`,
                            active: true,
                        },
                    ]}
                />

                <div>
                    {/*<Suspense fallback={<CardsSkeleton />}>*/}
                    <Form profUserId={session.user.id}/>
                    {/*</Suspense>*/}
                </div>
            </main>
        );
    }


}
