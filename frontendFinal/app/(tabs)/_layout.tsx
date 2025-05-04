import { Tabs } from "expo-router"

export default () => {
    return (
        <Tabs>
            <Tabs.Screen name="home"/>
            <Tabs.Screen name="welcome"/>
            <Tabs.Screen name="error"/>
        </Tabs>
    )
}

