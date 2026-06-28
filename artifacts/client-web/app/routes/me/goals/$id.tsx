import { useParams } from 'react-router';
import { PlaceholderPage } from '~/components/pages';

export default function LearningGoalDetailRoute() {
    const { id } = useParams();

    return (
        <PlaceholderPage
            description="View and edit your goal, or generate a study plan."
            title={`Learning goal ${id ?? ''}`.trim()}
        />
    );
}
