


import {lusitana} from '@/app/ui/fonts';
import {auth} from "@/auth";
import Breadcrumbs from "@/app/ui/invoices/breadcrumbs";
import {fetchLearningPathData} from "@/app/lib/api/learningpath/learningpath";
import {LearningPathAccordion} from "@/app/ui/learn/learning-path-accordion";
import Loading from "@/app/learn/(overview)/loading";
import CourseMaterials from "@/app/ui/learn/courseMaterials";
import LearningProgress from "@/app/ui/learn/learningProgress";
import TabGroup from "@/app/ui/learn/learningPathTabGroup";
import {Feedback} from "@/app/lib/types/courses";
import {fetchFeedbacks} from "@/app/lib/api/courses/learningpath";


interface PageProps {
    params: {
        pathId: string;
    };
    searchParams: {
        courseId: string;
    };
}

export default async function Page({params, searchParams}: PageProps) {
    const session = await auth();

    const {
        initialAssessment,
        learningPath
    } = await fetchLearningPathData(params.pathId, searchParams.courseId);
    const feedbacks: Feedback[] = await fetchFeedbacks(learningPath.id.toString());
    const courseName = learningPath.course.subject;
    const user = session?.user;

    return (
        <main>
            <Breadcrumbs
                breadcrumbs={[
                    {label: 'Learningpaths', href: '/learn/path'},
                    {
                        label: `${courseName}`,
                        href: '/learn/path/{id}',
                        active: true,
                    },
                ]}
            />
            {user && (
                <TabGroup
                    userId={session?.user.id!}
                    learningPathId={learningPath.id.toString()}
                    course={learningPath.course}
                    feedbacks={feedbacks}
                />
            )}
            <div className="flex bg-white rounded-lg shadow-md border border-gray-200 p-6 mb-8">
                {user && (
                    <LearningPathAccordion
                        learningPath={learningPath}
                        initialAssessment={initialAssessment}
                        user={user}
                    />
                )}
            </div>
        </main>
    );
}

