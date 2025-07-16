import {NavInterface, UserRole} from "../types/navBar/NavInterface";

type RoleNavConfig = Record<UserRole, NavInterface[]>;


/**
 * NAV_CONFIG is a configuration object that defines the navigation items for different user roles.
 * Each role has its own set of navigation items, each with a link (href), an icon name, and a label.
 * This configuration is used to dynamically generate the navigation bar based on the user's role.
 */
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
        { href: '/delete-entities', iconName: 'remove-circle-outline', label: 'Delete Entities' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
        { href: '/update-lecture', iconName: 'bar-chart-outline', label: 'Update Lecture' },
        { href: '/teacher-management', iconName: 'people-outline', label: 'Teacher Management' },
    ],
    TECHNICAL_SERVICE: [
        { href: '/home', iconName: 'home-outline', label: 'Home' },
        { href: '/issue-reports', iconName: 'warning-outline', label: 'Issue Reports' },
        { href: '/issue-reports/assigned', iconName: 'construct-outline', label: 'Issue Reports Assigned' },
        { href: '/issue-reports/create', iconName: 'school-outline', label: 'Create Issue' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
    ]
};