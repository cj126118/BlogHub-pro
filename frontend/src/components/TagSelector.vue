<template>
  <div class="tag-selector">
    <el-tag
      v-for="tag in selectedTags"
      :key="tag"
      closable
      size="small"
      @close="removeTag(tag)"
    >
      {{ tag }}
    </el-tag>
    <el-input
      v-if="inputVisible"
      ref="inputRef"
      v-model="inputValue"
      size="small"
      class="tag-input"
      @keyup.enter="confirmTag"
      @blur="confirmTag"
    />
    <el-button v-else size="small" class="add-tag-btn" @click="showInput">
      + 添加标签
    </el-button>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  allTags: { type: Array, default: () => [] },
})

const emit = defineEmits(['update:modelValue'])

const selectedTags = ref([...props.modelValue])
const inputVisible = ref(false)
const inputValue = ref('')
const inputRef = ref(null)

function showInput() {
  inputVisible.value = true
  nextTick(() => inputRef.value?.focus())
}

function confirmTag() {
  const val = inputValue.value.trim()
  if (val && !selectedTags.value.includes(val)) {
    selectedTags.value.push(val)
    emit('update:modelValue', [...selectedTags.value])
  }
  inputVisible.value = false
  inputValue.value = ''
}

function removeTag(tag) {
  selectedTags.value = selectedTags.value.filter(t => t !== tag)
  emit('update:modelValue', [...selectedTags.value])
}
</script>

<style scoped>
.tag-selector {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}
.tag-input {
  width: 120px;
}
.add-tag-btn {
  border-style: dashed;
}
</style>
