<template>
    <div>首页</div>
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