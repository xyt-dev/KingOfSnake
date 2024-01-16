<template>
  <div class="container content-field">
    <div class="card">
      <div class="card-body">
          <h1>
            <slot name="h1">

            </slot>
          </h1>
        <slot name="default">

        </slot>
      </div>
    </div>
  </div>
</template>

<script setup>


</script>

<style scoped>
div.content-field {
  margin-top: 20px;
}
</style>