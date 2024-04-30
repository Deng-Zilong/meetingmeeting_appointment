<template>
    <div class="home">
        <div class="screen"></div>
        <div class="info">
            <div class="notice"></div>
            <div class="content">
                <div class="time"></div>
                <div class="rule">
                    <div></div>
                    <div></div>
                </div>
                <div class="right"></div>
            </div>
        </div>
        <div class="choose"></div>
        <div class="situation"></div>
    </div>
</template>
<script setup lang="ts">
    import {  onMounted } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import { useUserStore } from '@/stores/user'
    const userStore = useUserStore();
    const route = useRoute();
    const router = useRouter();
    
    onMounted(async() => {
        const code = decodeURIComponent(route.query.code as string);
        const token  = userStore.userInfo.access_token;
        if (!token) {
            router.replace('/login');
        } else {
            if (code != 'undefined') {
                await userStore.getQWUserInfo(code);
            }
        }
    });

</script>

<style lang="scss" scoped>
    .home {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        flex-wrap: wrap;
        flex: 1;
        box-sizing: border-box;
        div {
            width: 46.6875rem;
            height: 22.875rem;
            border-radius: 15px;
            margin: 0 .625rem;
        }
        .screen {
            margin-bottom: .625rem;
            background-image: url('@/assets/img/screen-bg.png');
            background-size: cover;
        }
        .info {
            width: 808px;
            background-color: #3268DC;
            margin-bottom: .625rem;
            .notice {
                width: 808px;
                height: 40px;
                margin: 0 0 .4125rem 0;
                border-radius: 6px;
                background: #FFFFFF;
                border: 2px solid rgba(18, 115, 219, 0.6);
            }
            .content {
                display: flex;
                .time {
                    width: 400px;
                    height: 327px;
                    flex-grow:0;
                    flex-shrink:0;
                    border-radius: 15px;
                    background-image: url('@/assets/img/time_bgc.png');
                    background-size: 100% 100%;
                    filter: opacity(0.800000011920929);
                }
                .rule {
                    display: inline-block;
                    div {
                        width: 235px;
                        height: 150px;
                        flex-grow:0;
                        flex-shrink:0;
                        border-radius: 15px;
                        background: #FFFFFF;
                        box-sizing: border-box;
                        border: 1px solid #3E78F4;
                    }
                }
                .right {
                    width: 133px;
                    height: 315px;
                    flex-grow:0;
                    flex-shrink:0;
                    border-radius: 15px;
                    opacity: 1;
                    background-image: url("@/assets/img/center_right_bgc.png");
                    background-size: 100% 100%;
                    filter: opacity(0.6000000238418579);
                }
            }
        }
        .choose {
            background-color: #3A3A3A;
        }
        .situation {
            width: 808px;
            background-color: #409EFF;
        }
    }
</style>