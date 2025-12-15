<template>
  <transition name="fade">
    <div v-if="visible" class="toast" :class="type">
      <span class="msg">{{ msg }}</span>
      <button class="close-btn" @click="hide">×</button>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'Toast',
  props: {
    message: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'info' // info | success | error
    },
    duration: {
      type: Number,
      default: 2000
    }
  },
  data() {
    return {
      visible: false,
      msg: this.message,
      timer: null
    };
  },
  watch: {
    message: {
      immediate: true,
      handler(val) {
        this.msg = val;
      }
    }
  },
  methods: {
    show(text, kind = 'info') {
      this.msg = text;
      this.visible = true;
      this.$emit('update:type', kind);
      clearTimeout(this.timer);
      this.timer = setTimeout(() => {
        this.visible = false;
      }, this.duration);
    },
    hide() {
      this.visible = false;
      clearTimeout(this.timer);
    }
  },
  beforeDestroy() {
    clearTimeout(this.timer);
  }
};
</script>

<style scoped>
.toast {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 16px;
  border-radius: 10px;
  color: #fff;
  background: var(--gray-900);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 50;
}

.toast.success {
  background: #16a34a;
}

.toast.error {
  background: #dc2626;
}

.msg {
  white-space: nowrap;
}

.close-btn {
  border: none;
  background: transparent;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
  transform: translate(-50%, 6px);
}
</style>
