<template>
    <div id="container" class="info">
        <div class="top-box">
            <div class="top-title">
                <div @click="goHome">
                    <van-icon name="arrow-left" />
                    首页
                </div>
                <div @click="logout">
                    退出登录
                </div>
            </div>
            <div class="top-left">
                <van-image
                    cover
                    round
                    width="4rem"
                    height="4rem"
                    src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg"
                />
                <span class="user-name">用户名: XXXX</span>
            </div>
        </div>
        <div class="card-box">
            <!-- <van-cell is-link icon="contact" size="large" title="个人资料" /> -->
            <van-cell is-link to="/history" icon="eye" size="large" title="历史会议" />
            <van-cell is-link to="/group" icon="friends" size="large" title="群组管理" />
        </div>
    </div>
</template>

<script setup lang="ts">
    import { showSuccessToast } from 'vant';
    import { useRouter } from 'vue-router';
    import { deleteInfo } from '@/request/api/login';

    const router = useRouter();
    
    /**
     * @description 返回首页
     */
    const goHome = () => {
       router.push('/home');
    };

    /**
     * @description 退出登录
     */
    const logout = async() => {
        const userId = JSON.parse(localStorage.getItem('userInfo') as string)?.userId; // 用户id
        const res:any = await deleteInfo(userId);
        showSuccessToast('退出成功');
        localStorage.removeItem('userInfo');
        router.replace('/login');
    };
</script>

<style lang="scss" scoped>
 .info {
    display: flex;
    justify-content: center;
    .top-box {
        width: 100vw;
        background-color: $background-color;
        color: #fff;
        padding: 1rem 1rem 3.7rem;
        border-radius: .3125rem;
        position: relative;
        .top-title {
            display: flex;
            justify-content: space-between;
            margin-bottom: 2.5rem;
            cursor: pointer;
        }
        .top-left {
            display: flex;
            justify-content: flex-start;
            align-items: center;
            .user-name {
                margin-left: 1rem;
            }
        }
    }
    .card-box {
        width: 92.5vw;
        height: 18.5rem;
        position: absolute;
        top: 12rem;
        padding: .925rem 0;
        border-radius: .3125rem;
        background: #fff;
        box-sizing: border-box;
        border: .0625rem solid rgba(0, 102, 255, .5);
        box-shadow: 0 0 1.875rem #6869694d;
        ::v-deep(.van-cell--large) {
            padding: 1.0625rem 12px;
        }
        ::v-deep(.van-cell__left-icon) {
            font-size: 1.1rem;
            color: $icon-color;
            margin-right: 10px;
        }
    }
 }
</style>