import { useParams } from 'react-router';
import { PlaceholderPage } from '~/components/pages';

export default function StudyPlanRoute() {
    const { id } = useParams();

    return (
        <PlaceholderPage
            description="GenAI study plan with milestones and proposed tutors."
            title={`Study plan ${id ?? ''}`.trim()}
        />
    );
}
