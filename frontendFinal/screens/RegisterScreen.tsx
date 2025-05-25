import React from 'react';
import {FlatList, Text, TextInput, TouchableOpacity, View} from 'react-native';
import {styles} from "../css_styling/profile/RectangleProps";
import {Picker} from '@react-native-picker/picker';

// @ts-ignore
export const RegisterScreen = ({ email, username, password, role, universityId, universities, selectedUniversity, searchQuery, onEmailChange, onUsernameChange, onPasswordChange, onRoleChange, onUniversityChange, onSearchChange, onUniversitySelect, onRegister, onNavigateToLogin}) => {
    return (
        <View style={styles.container}>
            <View style={styles.card}>
                <Text style={styles.title}>Register</Text>

                {/* Row for email, username, and password */}
                <View style={styles.inputRow}>
                    <TextInput
                        style={[styles.searchInput, styles.inputRowItem]}
                        placeholder="Email"
                        value={email}
                        onChangeText={onEmailChange}
                    />
                    <TextInput
                        style={[styles.searchInput, styles.inputRowItem]}
                        placeholder="Username"
                        value={username}
                        onChangeText={onUsernameChange}
                    />
                    <TextInput
                        style={[styles.searchInput, styles.inputRowItem]}
                        placeholder="Password"
                        value={password}
                        onChangeText={onPasswordChange}
                        secureTextEntry
                    />
                </View>

                <Text style={styles.centerMiddleText}>Select your role:</Text>
                <View style={styles.pickerContainer}>
                    <Picker
                        selectedValue={role}
                        onValueChange={onRoleChange}
                        style={styles.picker}
                        dropdownIconColor="#666"
                        mode="dropdown"
                    >
                        <Picker.Item label="Student" value="STUDENT" />
                        <Picker.Item label="Teacher" value="TEACHER" />
                        <Picker.Item label="Technical Services" value="TECHNICAL_SERVICES" />
                    </Picker>
                </View>
                <View style={styles.universitySearchContainer}>
                    <Text style={styles.centerMiddleText}>Search for your university:</Text>
                    <TextInput
                        style={styles.searchInput}
                        placeholder="Search universities..."
                        value={searchQuery}
                        onChangeText={onSearchChange}
                    />
                    {universities.length > 0 && (
                        <View style={styles.universityResultsContainer}>
                            <FlatList
                                data={universities}
                                keyExtractor={(item) => item.id.toString()}
                                renderItem={({ item }) => (
                                    <TouchableOpacity
                                        style={styles.itemSearch}
                                        onPress={() => onUniversitySelect(item)}
                                    >
                                        <Text style={styles.itemText}>{item.name}</Text>
                                    </TouchableOpacity>
                                )}
                            />
                        </View>
                    )}
                    <View style={styles.buttonsContainer}>
                        <TouchableOpacity
                            style={styles.loginRegisterButton}
                            onPress={onRegister}
                        >
                            <Text style={styles.loginRegisterButtonText}>Register</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            style={styles.loginRegisterButton}
                            onPress={onNavigateToLogin}
                        >
                            <Text style={styles.loginRegisterButtonText}>Go to Login</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </View>
        </View>
    );
}