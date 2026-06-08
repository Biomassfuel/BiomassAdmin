<template>
    <div :class="['sidebar-theme-wrapper', {'has-logo':showLogo}, sideTheme]" :style="{ backgroundColor: menuBackground }">
        <logo v-if="showLogo" :collapse="isCollapse" />
        <el-scrollbar :class="sideTheme" wrap-class="scrollbar-wrapper">
            <el-menu
                :default-active="activeMenu"
                :collapse="isCollapse"
                :background-color="menuBackground"
                :text-color="menuTextColor"
                :unique-opened="true"
                :active-text-color="activeTextColor"
                :collapse-transition="false"
                mode="vertical"
            >
                <sidebar-item
                    v-for="(route, index) in sidebarRouters"
                    :key="route.path  + index"
                    :item="route"
                    :base-path="route.path"
                />
            </el-menu>
        </el-scrollbar>
    </div>
</template>

<script>
import { mapGetters, mapState } from "vuex"
import Logo from "./Logo"
import SidebarItem from "./SidebarItem"
import variables from "@/assets/styles/variables.scss"

export default {
    components: { SidebarItem, Logo },
    computed: {
        ...mapState(["settings"]),
        ...mapGetters(["sidebarRouters", "sidebar"]),
        activeMenu() {
            const route = this.$route
            const { meta, path } = route
            // if set path, the sidebar will highlight the path you set
            if (meta.activeMenu) {
                return meta.activeMenu
            }
            if (path.indexOf('/detail/') > -1) {
                return path.substring(0, path.indexOf('/detail/'))
            }
            return path
        },
        showLogo() {
            return this.$store.state.settings.sidebarLogo
        },
        variables() {
            return variables
        },
        isCollapse() {
            return !this.sidebar.opened
        },
        sideTheme() {
            return this.$store.state.settings.sideTheme || 'theme-light'
        },
        menuBackground() {
            return this.sideTheme === 'theme-light' ? variables.menuLightBackground : variables.menuBackground
        },
        menuTextColor() {
            return this.sideTheme === 'theme-light' ? variables.menuLightColor : variables.menuColor
        },
        activeTextColor() {
            return this.sideTheme === 'theme-light' ? this.settings.theme : variables.menuColorActive
        }
    }
}
</script>
