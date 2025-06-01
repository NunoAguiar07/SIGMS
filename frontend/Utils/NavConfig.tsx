import {NavInterface, UserRole} from "../interfaces/NavInterface";

type RoleNavConfig = Record<UserRole, NavInterface[]>;

export const NAV_CONFIG: RoleNavConfig = {
    STUDENT: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/calendar', iconName: 'calendar-outline', label: 'Calendar' },
        { href: '/joinSubject', iconName: 'checkbox-outline', label: 'Classes' },
        { href: '/notifications', iconName: 'notifications-outline', label: 'Notifications' },
        { href: '/rooms', iconName: 'school-outline', label: 'Classrooms' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
    ],
    TEACHER: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/calendar', iconName: 'calendar-outline', label: 'Calendar' },
        { href: '/joinSubject', iconName: 'checkbox-outline', label: 'Classes' },
        { href: '/notifications', iconName: 'notifications-outline', label: 'Notifications' },
        { href: '/rooms', iconName: 'school-outline', label: 'Classrooms' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
    ],
    ADMIN: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/accessRoles', iconName: 'shield-checkmark-outline', label: 'Approvals' },
        { href: '/rooms', iconName: 'school-outline', label: 'Classrooms' },
        { href: '/generate-entities', iconName: 'add-circle-outline', label: 'Subjects' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
    ],
    TECHNICAL_SERVICE: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/issue-reports', iconName: 'warning-outline', label: 'Issue Reports' },
        { href: '/issue-reports/assigned', iconName: 'construct-outline', label: 'Issue Reports Assigned' },
        { href: '/rooms', iconName: 'school-outline', label: 'Classrooms' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
    ]
};