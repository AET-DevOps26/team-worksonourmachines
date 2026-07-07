import { useParams } from 'react-router';
import { PlaceholderPage } from '~/components/pages';

export default function ChatThreadRoute() {
    const { id } = useParams();

    return (
        <PlaceholderPage description="Conversation thread and message composer." title={`Chat ${id ?? ''}`.trim()} />
    );
}
