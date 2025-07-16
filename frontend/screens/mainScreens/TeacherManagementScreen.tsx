import React from "react";
import { ScrollView } from "react-native";
import { Ionicons } from "@expo/vector-icons";

import {
    TableContainer,
    TableHeader,
    TableColumn,
    TableRow,
    HeaderText,
    CellText,
} from "../css_styling/common/Tables";
import { ActionButton } from "../css_styling/common/Buttons";
import { Card, RowContainer, CenteredContainer } from "../css_styling/common/NewContainers";
import { Title } from "../css_styling/common/Typography";
import { PaginationControls } from "./UnassignedIssuesScreen";
import { TeacherScreenProps } from "../types/TeacherScreenProps";
import { OfficePickerModal } from "../components/teacherManagement/OfficePickerModal";

export const TeacherManagementScreen = ({
                                            teachers,
                                            onAssign,
                                            onUnassign,
                                            onNext,
                                            onPrevious,
                                            currentPage,
                                            hasNext,
                                            officeRooms,
                                            onNextRoomPage,
                                            onPreviousRoomPage,
                                            hasNextRoomPage,
                                            currentRoomPage,
                                            modalVisible,
                                            openModal,
                                            closeModal,
                                            handleSelectRoom,
                                            isRoomsLoading
                                        }: TeacherScreenProps
) => {
    return (
        <>
            <CenteredContainer flex={1} padding="md">
                <Card shadow="medium" width="95%" height="85%" alignItems="center" gap="md">
                    <Title>Teacher Management</Title>
                    <TableContainer>
                        <TableHeader>
                            <TableColumn width={3} first align="left">
                                <HeaderText>Name</HeaderText>
                            </TableColumn>
                            <TableColumn width={4} align="left">
                                <HeaderText>Email</HeaderText>
                            </TableColumn>
                            <TableColumn width={3} align="center">
                                <HeaderText>Office</HeaderText>
                            </TableColumn>
                            <TableColumn width={3} last align="center">
                                <HeaderText>Actions</HeaderText>
                            </TableColumn>
                        </TableHeader>
                        <ScrollView showsVerticalScrollIndicator={false}>
                            {teachers.map((teacher) => (
                                <TableRow key={teacher.user.id}>
                                    <TableColumn width={3} first align="left">
                                        <CellText numberOfLines={1}>{teacher.user.username}</CellText>
                                    </TableColumn>
                                    <TableColumn width={4} align="left">
                                        <CellText numberOfLines={1}>{teacher.user.email}</CellText>
                                    </TableColumn>
                                    <TableColumn width={3} align="center">
                                        <CellText>{teacher.office?.room?.name || "None"}</CellText>
                                    </TableColumn>
                                    <TableColumn width={3} last align="center">
                                        <RowContainer justifyContent="center">
                                            <ActionButton
                                                onPress={() =>
                                                    teacher.office
                                                        ? onUnassign(teacher.user.id, teacher.office.room.id)
                                                        : openModal(teacher.user.id)
                                                }
                                                variant={teacher.office ? "error" : "primary"}
                                            >
                                                <Ionicons
                                                    name={teacher.office ? "close" : "add"}
                                                    size={16}
                                                    color="white"
                                                />
                                            </ActionButton>
                                        </RowContainer>
                                    </TableColumn>
                                </TableRow>
                            ))}
                        </ScrollView>
                    </TableContainer>
                </Card>

                <PaginationControls
                    currentPage={currentPage}
                    hasNext={hasNext}
                    onNext={onNext}
                    onPrevious={onPrevious}
                />
            </CenteredContainer>

            <OfficePickerModal
                visible={modalVisible}
                onClose={closeModal}
                onSelect={handleSelectRoom}
                officeRooms={officeRooms}
                onNext={onNextRoomPage}
                onPrevious={onPreviousRoomPage}
                hasNext={hasNextRoomPage}
                currentPage={currentRoomPage}
                isLoading={isRoomsLoading}
            />
        </>
    );
};
