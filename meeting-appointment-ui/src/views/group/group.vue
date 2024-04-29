<template>
    <div class="container group">
        <div class="theme">
            <div class="left">
                <el-input v-model="input" style="width: 240px" placeholder="请输入" />
                <div>请输入群组名称</div>
            </div>
            <div class="center">
                <el-icon class="plus-icon"><Plus /></el-icon>
                <span></span>
                <div>添加人员</div>
            </div>
            <div class="right"> 创 建</div>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import { ref } from 'vue'
    const input = ref('')
</script>
<style scoped lang="scss">
    .group {
        background-color: #f5f5f5;
        .theme {
            display: flex;
            justify-content: space-around;
            align-items: center;
            .left {
                width: 55rem;
                div {
                    width: 12.5rem;
                }
            }
            .center {
                width: 32.5rem;
                padding-left: .625rem;
                .plus-icon {
                    width: 1.875rem;
                    height: 1.875rem;
                    opacity: 1;
                    color: #409EFF;
                    background: #ECF2FF;
                    cursor: pointer;
                }
            }
            .left, .center {
                height: 2.8125rem;
                border-radius: .5rem;
                opacity: 1;
                background: #FFFFFF;
                box-shadow: 0 .1875rem .375rem 0 rgba(0, 0, 0, 0.08);
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .right {
                transition: transform 0.2s ease;
                user-select: none;
                &:active {
                    transform: scale(0.96);
                }
            }
            .right, .center div {
                width: 9.375rem;
            }
            .right, .left div, .center div {
                height: 2.8125rem;
                line-height: 2.8125rem;
                text-align: center;
                color: #FFFFFF;
                border-radius: .5rem;
                opacity: 1;
                background: #409EFF;
                cursor: pointer;
            }
        }
    }
</style>