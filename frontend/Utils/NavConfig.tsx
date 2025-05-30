import {NavInterface, UserRole} from "../interfaces/NavInterface";

type RoleNavConfig = Record<UserRole, NavInterface[]>;

export const NAV_CONFIG: RoleNavConfig = {
    STUDENT: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/calendar', iconName: 'calendar-outline', label: 'Calendar' },
        { href: '/joinSubject', iconName: 'checkbox-outline', label: 'Classes' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
        { href: '/notifications', iconName: 'notifications-outline', label: 'Notifications' },
        { href: '/rooms', iconName: 'school-outline', label: 'Classrooms' },
    ],
    TEACHER: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/calendar', iconName: 'calendar-outline', label: 'Calendar' },
        { href: '/joinSubject', iconName: 'checkbox-outline', label: 'Classes' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
        { href: '/notifications', iconName: 'notifications-outline', label: 'Notifications' },
        { href: '/rooms', iconName: 'school-outline', label: 'Classrooms' },
    ],
    ADMIN: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/accessRoles', iconName: 'shield-checkmark-outline', label: 'Approvals' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
        { href: '/rooms', iconName: 'school-outline', label: 'Classrooms' },
    ],
    TECHNICAL_SERVICE: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/issue-reports', iconName: 'warning-outline', label: 'Issue Reports' },
        { href: '/issue-reports/assigned', iconName: 'construct-outline', label: 'Issue Reports Assigned' },
        { href: '/rooms', iconName: 'school-outline', label: 'Classrooms' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
    ]
};