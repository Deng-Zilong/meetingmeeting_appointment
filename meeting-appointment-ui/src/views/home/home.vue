<template>
    <div class="home">
        <div class="screen"></div>
        <div class="info">
            <div class="notice"></div>
            <div class="time"></div>
        </div>
        <div class="choose"></div>
        <div class="situation"></div>
    </div>
</template>
<script setup lang="ts">
import Cookies from 'js-cookie';
import { qwLogin } from '@/request/api/login';
import {  onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
    onMounted(async() => {
        const code = decodeURIComponent(route.query.code as string);
        let token  = Cookies.get('token');
        // if (!token) {
        //     router.replace('/login');
        // } else {
            qwLogin({code})
                .then((res: any) => {
                    console.log(res, "request");
                    
                })
                .catch((err: any) => { 
                    console.log(err, "error");
                    
                })
        // }
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
                margin: 0;
                border-radius: 6px;
                background: #FFFFFF;
                border: 2px solid rgba(18, 115, 219, 0.6);
            }
            .time {
                width: 400px;
                height: 315px;
                border-radius: 15px;
                background-image: url('@/assets/img/time_bgc.png');
                background-size: 100% 100%;
                filter: opacity(0.800000011920929);
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