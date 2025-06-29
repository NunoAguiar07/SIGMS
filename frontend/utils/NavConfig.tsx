import {NavInterface, UserRole} from "../types/navBar/NavInterface";

type RoleNavConfig = Record<UserRole, NavInterface[]>;

export const NAV_CONFIG: RoleNavConfig = {
    STUDENT: [
        { href: '/home', iconName: 'home-outline', label: 'Home' },
        { href: '/calendar', iconName: 'calendar-outline', label: 'Calendar' },
        { href: '/join-subject', iconName: 'checkbox-outline', label: 'Classes' },
        { href: '/issue-reports/create', iconName: 'school-outline', label: 'Create Issue' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
        { href: '/study-rooms', iconName: 'book-outline', label: 'Study Rooms' },
    ],
    TEACHER: [
        { href: '/home', iconName: 'home-outline', label: 'Home' },
        { href: '/calendar', iconName: 'calendar-outline', label: 'Calendar' },
        { href: '/join-subject', iconName: 'checkbox-outline', label: 'Classes' },
        { href: '/issue-reports/create', iconName: 'school-outline', label: 'Create Issue' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
        { href: '/update-lecture', iconName: 'bar-chart-outline', label: 'Update Lecture' },

    ],
    ADMIN: [
        { href: '/home', iconName: 'home-outline', label: 'Home' },
        { href: '/access-roles', iconName: 'shield-checkmark-outline', label: 'Approvals' },
        { href: '/issue-reports/create', iconName: 'school-outline', label: 'Create Issue' },
        { href: '/generate-entities', iconName: 'add-circle-outline', label: 'Add Entities' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
        { href: '/update-lecture', iconName: 'bar-chart-outline', label: 'Update Lecture' },
    ],
    TECHNICAL_SERVICE: [
        { href: '/home', iconName: 'home-outline', label: 'Home' },
        { href: '/issue-reports', iconName: 'warning-outline', label: 'Issue Reports' },
        { href: '/issue-reports/assigned', iconName: 'construct-outline', label: 'Issue Reports Assigned' },
        { href: '/issue-reports/create', iconName: 'school-outline', label: 'Create Issue' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
    ]
};