import React from "react";
import { Modal, View, FlatList, TouchableOpacity, Text, StyleSheet, ActivityIndicator } from "react-native";
import {RoomInterface} from "../../../types/RoomInterface";
import {RowContainer} from "../../css_styling/common/NewContainers";
import {ActionButton} from "../../css_styling/common/Buttons";

interface Props {
    visible: boolean;
    onClose: () => void;
    onSelect: (roomId: number) => void;
    officeRooms: RoomInterface[];
    onNext: () => void;
    onPrevious: () => void;
    hasNext: boolean;
    currentPage: number;
    isLoading: boolean;
}

export const OfficePickerModal = ({
                                      visible,
                                      onClose,
                                      onSelect,
                                      officeRooms,
                                      onNext,
                                      onPrevious,
                                      hasNext,
                                      currentPage,
                                      isLoading
                                  }: Props) => {
    return (
        <Modal visible={visible} transparent animationType="slide">
            <View style={styles.modalOverlay}>
                <View style={styles.modalContainer}>
                    <Text style={styles.title}>
                        Select an Office {currentPage !== undefined && currentPage !== null ? `(Page ${currentPage + 1})` : ""}
                    </Text>

                    <FlatList
                        data={officeRooms}
                        keyExtractor={(item) => item.id.toString()}
                        renderItem={({ item }) => (
                            <TouchableOpacity
                                onPress={() => {
                                    if (!isLoading) {
                                        onSelect(item.id);
                                        onClose();
                                    }
                                }}
                                style={styles.listItem}
                                disabled={isLoading}
                            >
                                <Text>{item.name}</Text>
                            </TouchableOpacity>
                        )}
                    />

                    <RowContainer justifyContent="space-between" style={styles.paginationContainer}>
                        <ActionButton
                            onPress={onPrevious}
                            disabled={currentPage === 0 || isLoading}
                            style={styles.button}
                        >
                            Previous
                        </ActionButton>
                        <ActionButton
                            onPress={onNext}
                            disabled={!hasNext || isLoading}
                            style={styles.button}
                        >
                            Next
                        </ActionButton>
                    </RowContainer>

                    <TouchableOpacity onPress={onClose} style={{ marginTop: 12 }} disabled={isLoading}>
                        <Text style={{ color: "blue", textAlign: "center" }}>Cancel</Text>
                    </TouchableOpacity>

                    {isLoading && (
                        <View style={styles.loadingOverlay}>
                            <ActivityIndicator size="large" color="#007AFF" />
                        </View>
                    )}
                </View>
            </View>
        </Modal>
    );
};

const styles = StyleSheet.create({
    modalOverlay: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "rgba(0,0,0,0.5)"
    },
    modalContainer: {
        backgroundColor: "white",
        borderRadius: 8,
        padding: 20,
        width: "80%",
        maxHeight: "70%",
        position: "relative",
    },
    title: {
        fontSize: 18,
        fontWeight: "bold",
        marginBottom: 10
    },
    listItem: {
        padding: 12,
        borderBottomWidth: 1,
        borderColor: "#eee"
    },
    paginationContainer: {
        marginTop: 12,
        paddingHorizontal: 10,
        width: "100%"
    },
    button: {
        paddingHorizontal: 15,
        paddingVertical: 10,
        minWidth: 100,
        textAlign: "center"
    },
    loadingOverlay: {
        ...StyleSheet.absoluteFillObject,
        backgroundColor: "rgba(255, 255, 255, 0.7)",
        justifyContent: "center",
        alignItems: "center",
        borderRadius: 8,
        zIndex: 10,
    }
});
