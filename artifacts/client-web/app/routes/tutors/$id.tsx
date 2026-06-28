import { useParams } from 'react-router';
import { PlaceholderPage } from '~/components/pages';

export default function TutorProfileRoute() {
    const { id } = useParams();

    return (
        <PlaceholderPage
            description="Tutor profile, coverage, ratings, and contact options."
            title={`Tutor ${id ?? ''}`.trim()}
        />
    );
}
