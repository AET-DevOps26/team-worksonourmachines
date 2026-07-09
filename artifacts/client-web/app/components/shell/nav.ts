type NavLink = {
    href: string;
    label: string;
};

type NavSection = {
    title: string;
    links: NavLink[];
};

type ProfileMenuGroup = {
    label?: string;
    links: NavLink[];
};

export const publicNavLinks: NavLink[] = [
    { href: '/#how-it-works', label: 'How it works' },
    { href: '/become-a-tutor', label: 'For tutors' },
    { href: '/modules', label: 'Modules' },
];

export const appNavLinks: NavLink[] = [
    { href: '/discover', label: 'Discover' },
    { href: '/me/goals', label: 'Goals' },
];

export const footerSections: NavSection[] = [
    {
        links: [
            { href: '/discover', label: 'Find tutors' },
            { href: '/modules', label: 'Modules' },
            { href: '/me/goals', label: 'Learning goals' },
        ],
        title: 'Product',
    },
    {
        links: [
            { href: '/become-a-tutor', label: 'Become a tutor' },
            { href: '/tutor/apply', label: 'Apply now' },
            { href: '/tutor/dashboard', label: 'Tutor dashboard' },
        ],
        title: 'For tutors',
    },
    {
        links: [
            { href: '/about', label: 'About' },
            { href: '/terms', label: 'Terms of Service' },
            { href: '/privacy', label: 'Privacy Policy' },
        ],
        title: 'Support',
    },
];

function hasRole(roles: readonly string[], role: string) {
    return roles.includes(role);
}

export function getProfileMenuGroups(roles: readonly string[]): ProfileMenuGroup[] {
    const groups: ProfileMenuGroup[] = [
        {
            links: [{ href: '/me/profile', label: 'My profile' }],
        },
    ];

    if (hasRole(roles, 'student')) {
        groups.push({
            label: 'Learning',
            links: [
                { href: '/me/goals', label: 'My goals' },
                { href: '/discover', label: 'Discover tutors' },
                { href: '/modules', label: 'Browse modules' },
            ],
        });
    }

    if (hasRole(roles, 'tutor')) {
        groups.push({
            label: 'Tutoring',
            links: [
                { href: '/tutor/dashboard', label: 'Tutor dashboard' },
                { href: '/tutor/profile', label: 'Tutor profile' },
            ],
        });
    } else {
        groups.push({
            label: 'Tutoring',
            links: [{ href: '/tutor/apply', label: 'Apply as tutor' }],
        });
    }

    groups.push({
        label: 'Messages',
        links: [{ href: '/chat', label: 'Messages' }],
    });

    if (hasRole(roles, 'admin')) {
        groups.push({
            label: 'Administration',
            links: [
                { href: '/admin', label: 'Admin dashboard' },
                { href: '/admin/tutor-approvals', label: 'Tutor approvals' },
                { href: '/admin/modules', label: 'Manage modules' },
            ],
        });
    }

    return groups;
}
