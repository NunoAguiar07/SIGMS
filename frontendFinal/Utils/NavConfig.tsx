import {NavInterface, UserRole} from "../interfaces/NavInterface";

type RoleNavConfig = Record<UserRole, NavInterface[]>;

export const NAV_CONFIG: RoleNavConfig = {
    STUDENT: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/calendar', iconName: 'calendar-outline', label: 'Calendar' },
        { href: '/joinSubject', iconName: 'checkbox-outline', label: 'Classes' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
        { href: '/notifications', iconName: 'notifications-outline', label: 'Notifications' }
    ],
    TEACHER: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/calendar', iconName: 'calendar-outline', label: 'Calendar' },
        { href: '/joinSubject', iconName: 'checkbox-outline', label: 'Classes' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' },
        { href: '/notifications', iconName: 'notifications-outline', label: 'Notifications' }
    ],
    ADMIN: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/accessRoles', iconName: 'shield-checkmark-outline', label: 'Approvals' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' }
    ],
    TECHNICAL_SERVICES: [
        { href: '/userHome', iconName: 'home-outline', label: 'Home' },
        { href: '/profile', iconName: 'person-outline', label: 'Profile' }
    ]
};