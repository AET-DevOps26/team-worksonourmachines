import { useParams } from 'react-router';
import { PlaceholderPage } from '~/components/pages';

export default function ModuleDetailRoute() {
    const { code } = useParams();

    return (
        <PlaceholderPage
            description={`Module detail and topic overview for ${code ?? 'this module'}.`}
            title={code ?? 'Module'}
        />
    );
}
