import {
    BanknotesIcon,
    ClockIcon,
    UserGroupIcon,
    InboxIcon,
} from '@heroicons/react/24/outline';
import {lusitana} from '@/app/ui/fonts';
import {enrollUserToCourse, fetchCourses} from "@/app/lib/api/courses/course";
import {Course, LearningPath, Topic} from "@/app/lib/types/courses";
import {User} from "@/app/lib/types/users";
import TopicCollapse from "@/app/ui/courses/topic-collapse";
import Link from "next/link";
import {auth} from "@/auth";
import EnrollButton from "@/app/ui/courses/enroll-button";
import {fetchUser} from "@/app/lib/api/user/user";
import {fetchLearningPaths} from "@/app/lib/api/learningpath/learningpath";

const iconMap = {
    collected: BanknotesIcon,
    customers: UserGroupIcon,
    pending: ClockIcon,
    invoices: InboxIcon,
};

interface CardProps {
    id: string;
    course: Course;
    subject: string;
    description: string;
    topics: Topic[];
    prof: User;
    users: User[];
    currentUserId: string | undefined;
    currentUser: User | undefined;
    showEnrollButton: boolean;
    // isLearningPath: boolean;
}

interface WrapperProps {
    userId?: string | undefined
}

export default async function CourseCardWrapper({userId}: WrapperProps) {
    const courses: Course[] = await fetchCourses(userId);
    // const user: any = await fetchUser(userId);
    const session = await auth();
    const isLearning = userId !== undefined;
    const paths: LearningPath[] = await fetchLearningPaths(session?.user.id);

    if (courses.length === 0) {
        return <p>No courses exist yet.</p>;
    }

        return (
            <>
                {courses.map((course) => {
                    const pathExists = paths.some(path => path.course.id === course.id);
                    console.log("YLYOYOOYOYOY")
                    console.log(course.id)
                    console.log(pathExists)
                    console.log("YLYOYOOYOYOY")
                    return (
                    <Card
                        key={course.id}
                        course={course}
                        id={course.id.toString()}
                        subject={course.subject}
                        description={course.description}
                        topics={course.topics}
                        prof={course.prof}
                        users={course.users}
                        currentUserId={session?.user.id}
                        currentUser={session?.user}
                        showEnrollButton={!pathExists}
                    />
                )})}
            </>
        );
}

export function Card({id, course, subject, description, topics, prof, users, currentUserId, currentUser, showEnrollButton}: CardProps) {
    const Icon = iconMap["collected"];
    const isTeacher = currentUser ? currentUser.role === 'teacher' : 'teacher';
    console.log("CARD COURSE", course);
    return (
        <div>
            {/*<div className="card-container border p-2">*/}
            <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6 mb-8">
                <div className="flex flex-col justify-between p-4">
                    <div className="flex p-4">
                        {Icon && <Icon className="h-5 w-5 text-gray-700"/>}
                        <h3 className="ml-2 text-2xl font-medium">{subject}</h3>
                    </div>
                    <p className="ml-auto text-sm font-light" style={{alignSelf: 'flex-end'}}>{prof.name}</p>
                </div>

                <div className="card-content">
                    <p className={`${lusitana.className} rounded-xl bg-white px-4 py-8 text-center text-sm`}>
                        {description}
                    </p>
                </div>

                {isTeacher! && (
                    <>
                        <Link
                            href={`/dashboard/courses/${id}`}
                            className="ml-auto m-2 rounded-md bg-blue-500 px-4 py-2 text-sm text-white transition-colors hover:bg-blue-400"
                            passHref
                        >
                            Kurs bearbeiten
                        </Link>
                        <Link
                            href={`courses/${id}/dashboard`}
                            className="ml-auto m-2 rounded-md bg-blue-500 px-4 py-2 text-sm text-white transition-colors hover:bg-blue-400"
                        >
                            Kurs Statistiken
                        </Link>
                    </>
                )}

                {/*{!isTeacher && <EnrollButton currentUserId={currentUserId} courseId={id} />}*/}
                {(course && course.topics && course.assessment && showEnrollButton) &&
                    // <EnrollButton currentUserId={currentUserId} courseId={id} />
                 <EnrollButton currentUserId={currentUserId} courseId={id} />
                }

                <TopicCollapse topics={topics}/>
            </div>
        </div>
    );
}