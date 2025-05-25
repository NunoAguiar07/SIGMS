import React from "react";
import {Ionicons} from "@expo/vector-icons";

type IoniconName = React.ComponentProps<typeof Ionicons>['name'];

export interface NavInterface {
    href: string;
    iconName: IoniconName;
    label: string;
}

export type UserRole = 'STUDENT' | 'TEACHER' | 'ADMIN' | 'TECHNICAL_SERVICES';