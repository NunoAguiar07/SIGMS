import {FlatList, Text, TextInput, TouchableOpacity, View} from 'react-native';
import {commonStyles} from "../../css_styling/common/CommonProps";
import {universityStyles} from "../../css_styling/university/UniversityProps";
import {Picker} from '@react-native-picker/picker';
import {RegisterScreenType} from "../types/RegisterScreenType";

export const RegisterScreen = ({
    email,
    username,
    password,
    role,
    universityId,
    universities,
    selectedUniversity,
    searchQuery,
    onEmailChange,
    onUsernameChange,
    onPasswordChange,
    onRoleChange,
    onSearchChange,
    onUniversitySelect,
    onRegister,
    onNavigateToLogin,
}: RegisterScreenType) => {
    return (
        <View style={commonStyles.container}>
            <View style={commonStyles.card}>
                <Text style={commonStyles.title}>Register</Text>

                <View style={commonStyles.inputRow}>
                    <TextInput
                        style={[commonStyles.searchInput, commonStyles.inputRowItem]}
                        placeholder="Email"
                        value={email}
                        onChangeText={onEmailChange}
                    />
                    <TextInput
                        style={[commonStyles.searchInput, commonStyles.inputRowItem]}
                        placeholder="Username"
                        value={username}
                        onChangeText={onUsernameChange}
                    />
                    <TextInput
                        style={[commonStyles.searchInput, commonStyles.inputRowItem]}
                        placeholder="Password"
                        value={password}
                        onChangeText={onPasswordChange}
                        secureTextEntry
                    />
                </View>

                <Text style={commonStyles.centerMiddleText}>Select your role:</Text>
                <View style={commonStyles.pickerContainer}>
                    <Picker
                        selectedValue={role}
                        onValueChange={onRoleChange}
                        style={commonStyles.picker}
                        dropdownIconColor="#666"
                        mode="dropdown"
                    >
                        <Picker.Item label="Student" value="STUDENT" />
                        <Picker.Item label="Teacher" value="TEACHER" />
                        <Picker.Item label="Technical Services" value="TECHNICAL_SERVICE" />
                    </Picker>
                </View>

                <View style={universityStyles.universitySearchContainer}>
                    <Text style={commonStyles.centerMiddleText}>Search for your university:</Text>
                    <TextInput
                        style={commonStyles.searchInput}
                        placeholder="Search universities..."
                        value={searchQuery}
                        onChangeText={onSearchChange}
                    />
                    {universities.length > 0 && (
                        <View style={universityStyles.universityResultsContainer}>
                            <FlatList
                                data={universities}
                                keyExtractor={(item) => item.id.toString()}
                                renderItem={({ item }) => (
                                    <TouchableOpacity
                                        style={commonStyles.itemSearch}
                                        onPress={() => onUniversitySelect(item)}
                                    >
                                        <Text style={commonStyles.itemText}>{item.name}</Text>
                                    </TouchableOpacity>
                                )}
                            />
                        </View>
                    )}
                    <View style={commonStyles.buttonsContainer}>
                        <TouchableOpacity
                            style={commonStyles.loginRegisterButton}
                            onPress={onRegister}
                        >
                            <Text style={commonStyles.loginRegisterButtonText}>Register</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            style={commonStyles.loginRegisterButton}
                            onPress={onNavigateToLogin}
                        >
                            <Text style={commonStyles.loginRegisterButtonText}>Go to Login</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </View>
        </View>
    );
}